package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dao.WeeklyReportDao;
import com.ssafy.mvc.model.dao.WeeklyReportMapper;
import com.ssafy.mvc.model.dto.NutrientGuideDto;
import com.ssafy.mvc.model.dto.UserDto;
import com.ssafy.mvc.model.dto.WeeklyNutrientStatsDto;
import com.ssafy.mvc.model.dto.WeeklyReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ReportService {
    private final WeeklyReportMapper weeklyReportMapper;
    private final WeeklyReportDao weeklyReportDao;
    private final UserDao userDao;
    private final DiseaseService diseaseService;
    private final MealNutrientService mealNutrientService;
    private final OpenAiService openAiService;

    /**
     * 수동/스케줄러용: 주간 리포트 생성 + AI 추천
     */
    public WeeklyReportDto generateReport(int userId, LocalDate startDate, LocalDate endDate) {
        // 1. 일일 영양소 합계 계산
        mealNutrientService.calculateMealNutrientsForDateRange(userId, startDate, endDate);

        // 2. 주간 통계 조회
        List<WeeklyNutrientStatsDto> stats = weeklyReportMapper.getWeeklyStats(userId, startDate, endDate);

        // 3. 질병 가이드라인 조회
        Map<Integer, NutrientGuideDto> guidelines = diseaseService.getUserGuidelines(userId);

        // 4. 식사 횟수 계산
        int mealCount = weeklyReportMapper.getMealCount(userId, startDate, endDate);

        // 5. 영양소별 상태/준수율 계산
        for (WeeklyNutrientStatsDto stat : stats) {
            NutrientGuideDto guide = guidelines.get(stat.getNutrientId());
            if (guide != null) {
                stat.setStatus(calculateStatus(stat.getAvg(), guide));
                stat.setComplianceRate(calculateCompliance(stat.getAvg(), guide));
                stat.setRecommendation(guide.getRecommendation());
            } else {
                stat.setStatus("정보 없음");
                stat.setComplianceRate(BigDecimal.ZERO);
                stat.setRecommendation("가이드라인 없음");
            }
        }

        // 6. 우선순위 영양소 3개 선별
        List<WeeklyNutrientStatsDto> priorityNutrients = getTopPriorityNutrients(stats);

        // 7. 리포트 DTO 조립
        WeeklyReportDto report = WeeklyReportDto.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .mealCount(mealCount)
                .nutrients(stats)
                .priorityNutrients(priorityNutrients) // 우선순위 영양소 추가
                .healthScore(calculateHealthScore(stats))
                .build();

        // 8. 영양소 상태 요약 생성
        String nutrientSummary = generateNutrientSummary(priorityNutrients);
        report.setNutrientSummary(nutrientSummary);

        // 9. AI 기반 개인화된 건강 조언 생성
        try {
            String healthTips = generatePersonalizedHealthTips(userId, priorityNutrients, report.getHealthScore(), mealCount);
            report.setHealthTips(healthTips);
        } catch (Exception e) {
            System.err.println("건강 조언 생성 실패: " + e.getMessage());
            report.setHealthTips("규칙적인 식사와 균형 잡힌 영양 섭취를 위해 노력하세요.\n적절한 운동과 충분한 수면도 건강에 도움이 됩니다.\n궁금한 점은 전문가와 상담해보세요.");
        }

        // 10. 식단 추천 AI 생성
        try {
            String prompt = buildEnhancedPrompt(report);
            String dietRecommendation = openAiService.recommendDiet(prompt);
            report.setRecommendation(dietRecommendation);
        } catch (Exception e) {
            System.err.println("식단 추천 생성 실패: " + e.getMessage());
            report.setRecommendation("균형 잡힌 식단을 위해 다양한 영양소를 골고루 섭취하세요.");
        }

        // 11. DB 저장
        weeklyReportDao.insert(report);

        return report;
    }

    /**
     * 매일 새벽 2시에 자동 실행: 모든 사용자에 대해 지난주 리포트 생성 + AI 호출
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void scheduledWeeklyReport() {
        LocalDate end   = LocalDate.now().minusDays(1);
        LocalDate start = end.minusDays(6);

        // 모든 사용자 조회
        List<UserDto> users = userDao.select();
        for (UserDto user : users) {
            int uid = (int) user.getId();
            generateReport(uid, start, end);
        }
    }

    // 최신 리포트 조회
    public WeeklyReportDto getLatestReport(int userId) {
        return weeklyReportDao.selectByUserId(userId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    // 날짜 기반 리포트 조회
    public WeeklyReportDto getWeeklyReportByDate(int userId, LocalDate date) {
        return weeklyReportDao.selectByUserId(userId)
                .stream()
                .filter(report -> !date.isBefore(report.getStartDate()) && !date.isAfter(report.getEndDate()))
                .findFirst()
                .orElse(null);
    }

    // 건강 점수 계산
    private Integer calculateHealthScore(List<WeeklyNutrientStatsDto> stats) {
        if (stats == null || stats.isEmpty()) return 0;
        long optimalCount = stats.stream()
                .filter(stat -> "적정".equals(stat.getStatus()))
                .count();
        return (int) ((optimalCount * 100.0) / stats.size());
    }

    // 준수율 계산
    private BigDecimal calculateCompliance(BigDecimal avg, NutrientGuideDto guide) {
        BigDecimal min = guide.getMin();
        BigDecimal max = guide.getMax();
        if (avg == null || min == null || max == null) return BigDecimal.ZERO;
        try {
            if (guide.isRestriction()) {
                if (avg.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return max.multiply(BigDecimal.valueOf(100))
                        .divide(max, 2, RoundingMode.HALF_UP);
            } else {
                if (min.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return avg.multiply(BigDecimal.valueOf(100))
                        .divide(min, 2, RoundingMode.HALF_UP);
            }
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }

    // 상태 판정
    private String calculateStatus(BigDecimal avg, NutrientGuideDto guide) {
        if (avg == null) return "미확인";
        if (guide.isRestriction()) {
            if (guide.getMax() == null) return "미확인";
            return avg.compareTo(guide.getMax()) > 0 ? "초과" : "적정";
        } else {
            if (guide.getMin() == null) return "미확인";
            return avg.compareTo(guide.getMin()) < 0 ? "부족" : "적정";
        }
    }

    /**
     * AI 기반 식단 추천만 별도 생성 (기존 OpenAI 서비스 활용)
     */
    public String generateDietRecommendation(int userId, LocalDate startDate, LocalDate endDate) {
        // 1. 주간 통계 조회
        List<WeeklyNutrientStatsDto> stats = weeklyReportMapper.getWeeklyStats(userId, startDate, endDate);

        // 2. 질병 가이드라인 조회
        Map<Integer, NutrientGuideDto> guidelines = diseaseService.getUserGuidelines(userId);

        // 3. 식사 횟수 계산
        int mealCount = weeklyReportMapper.getMealCount(userId, startDate, endDate);

        // 4. 영양소별 상태 계산
        for (WeeklyNutrientStatsDto stat : stats) {
            NutrientGuideDto guide = guidelines.get(stat.getNutrientId());
            if (guide != null) {
                stat.setStatus(calculateStatus(stat.getAvg(), guide));
                stat.setComplianceRate(calculateCompliance(stat.getAvg(), guide));
                stat.setRecommendation(guide.getRecommendation());
            } else {
                stat.setStatus("정보 없음");
                stat.setComplianceRate(BigDecimal.ZERO);
                stat.setRecommendation("가이드라인 없음");
            }
        }
        // 5. 임시 리포트 DTO 생성 (DB 저장 없이)
        WeeklyReportDto tempReport = WeeklyReportDto.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .mealCount(mealCount)
                .nutrients(stats)
                .healthScore(calculateHealthScore(stats))
                .build();

        // 6. 기존 buildforgpt 메서드 활용해서 프롬프트 생성
        String prompt =  buildEnhancedPrompt(tempReport);

        // 7. 기존 OpenAI 서비스 호출
        return openAiService.recommendDiet(prompt);
    }

    // GPT 프롬프트용 문자열 생성
    private String buildEnhancedPrompt(WeeklyReportDto report) {
        StringBuilder sb = new StringBuilder();

        // 기본 정보
        sb.append("=== 사용자 주간 영양 분석 리포트 ===\n");
        sb.append("📅 분석 기간: ").append(report.getStartDate()).append(" ~ ").append(report.getEndDate()).append("\n");
        sb.append("🍽️ 총 식사 횟수: ").append(report.getMealCount()).append("회\n");
        sb.append("💯 건강 점수: ").append(report.getHealthScore()).append("점/100점\n\n");

        // 영양소별 상세 분석
        sb.append("=== 영양소별 섭취 현황 ===\n");

        List<String> deficientNutrients = new ArrayList<>();
        List<String> excessiveNutrients = new ArrayList<>();
        List<String> optimalNutrients = new ArrayList<>();

        for (WeeklyNutrientStatsDto stat : report.getNutrients()) {
            String status = stat.getStatus();
            sb.append("• ").append(stat.getNutrientName())
                    .append(": 평균 ").append(stat.getAvg())
                    .append(" (상태: ").append(status);

            if (stat.getComplianceRate() != null) {
                sb.append(", 준수율: ").append(stat.getComplianceRate()).append("%");
            }
            sb.append(")\n");

            // 카테고리별 분류
            switch (status) {
                case "부족": deficientNutrients.add(stat.getNutrientName()); break;
                case "초과": excessiveNutrients.add(stat.getNutrientName()); break;
                case "적정": optimalNutrients.add(stat.getNutrientName()); break;
            }
        }

        // 우선 개선 사항
        sb.append("\n=== 개선 필요 사항 ===\n");
        if (!deficientNutrients.isEmpty()) {
            sb.append("🔺 부족한 영양소: ").append(String.join(", ", deficientNutrients)).append("\n");
        }
        if (!excessiveNutrients.isEmpty()) {
            sb.append("🔻 과다한 영양소: ").append(String.join(", ", excessiveNutrients)).append("\n");
        }
        if (!optimalNutrients.isEmpty()) {
            sb.append("✅ 적정한 영양소: ").append(String.join(", ", optimalNutrients)).append("\n");
        }

        // 식사 빈도 평가
        if (report.getMealCount() < 14) { // 주 2회 평균 미만
            sb.append("\n⚠️ 주의: 식사 빈도가 낮습니다. 규칙적인 식사가 필요합니다.\n");
        }

        sb.append("\n=== 추천 요청 ===\n");
        sb.append("위 분석을 바탕으로 다음 주 식단 개선 방안을 제시해주세요.\n");

        return sb.toString();
    }

    /**
     * 주요 영양소 3개 선별 (우선순위: 부족 > 초과 > 적정)
     */
    public List<WeeklyNutrientStatsDto> getTopPriorityNutrients(List<WeeklyNutrientStatsDto> allNutrients) {
        List<WeeklyNutrientStatsDto> deficient = allNutrients.stream()
                .filter(n -> "부족".equals(n.getStatus()))
                .limit(3)
                .toList();

        List<WeeklyNutrientStatsDto> excessive = allNutrients.stream()
                .filter(n -> "초과".equals(n.getStatus()))
                .limit(3 - deficient.size())
                .toList();

        List<WeeklyNutrientStatsDto> optimal = allNutrients.stream()
                .filter(n -> "적정".equals(n.getStatus()))
                .limit(3 - deficient.size() - excessive.size())
                .toList();

        List<WeeklyNutrientStatsDto> result = new ArrayList<>();
        result.addAll(deficient);
        result.addAll(excessive);
        result.addAll(optimal);

        return result;
    }

    /**
     * AI 기반 개인화된 건강 조언 생성
     */
    public String generatePersonalizedHealthTips(int userId, List<WeeklyNutrientStatsDto> priorityNutrients, int healthScore, int mealCount) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("=== 개인화된 건강 조언 요청 ===\n");
        prompt.append("사용자의 주간 영양 상태를 바탕으로 실천 가능한 건강 조언 3가지를 작성해주세요.\n\n");

        prompt.append("현재 상태:\n");
        prompt.append("- 건강 점수: ").append(healthScore).append("점/100점\n");
        prompt.append("- 주간 식사 횟수: ").append(mealCount).append("회\n");
        prompt.append("- 주요 영양소 상태:\n");

        for (WeeklyNutrientStatsDto nutrient : priorityNutrients) {
            prompt.append("  * ").append(nutrient.getNutrientName())
                    .append(": ").append(nutrient.getStatus())
                    .append(" (평균 ").append(nutrient.getAvg()).append(")\n");

            if (nutrient.getRecommendation() != null) {
                prompt.append("    → ").append(nutrient.getRecommendation()).append("\n");
            }
        }

        prompt.append("\n요청사항:\n");
        prompt.append("1. 위 영양소 상태를 개선할 수 있는 구체적인 조언 3가지\n");
        prompt.append("2. 일상생활에서 쉽게 실천할 수 있는 내용으로 구성\n");
        prompt.append("3. 각 조언은 한 문장으로 간단명료하게 작성\n");
        prompt.append("4. 고령자도 이해하기 쉬운 친근한 톤으로 작성\n");
        prompt.append("5. 응답 형식: 번호 없이 3줄로 구분해서 작성\n");

        return openAiService.recommendDiet(prompt.toString());
    }

    /**
     * 영양소별 상태 요약 생성
     */
    public String generateNutrientSummary(List<WeeklyNutrientStatsDto> priorityNutrients) {
        if (priorityNutrients.isEmpty()) {
            return "영양소 데이터가 부족합니다.";
        }

        long deficientCount = priorityNutrients.stream().filter(n -> "부족".equals(n.getStatus())).count();
        long excessiveCount = priorityNutrients.stream().filter(n -> "초과".equals(n.getStatus())).count();
        long optimalCount = priorityNutrients.stream().filter(n -> "적정".equals(n.getStatus())).count();

        StringBuilder summary = new StringBuilder();

        if (deficientCount > 0) {
            List<String> deficientNames = priorityNutrients.stream()
                    .filter(n -> "부족".equals(n.getStatus()))
                    .map(WeeklyNutrientStatsDto::getNutrientName)
                    .collect(Collectors.toList());
            summary.append(String.join(", ", deficientNames)).append(" 섭취가 부족합니다. ");
        }

        if (excessiveCount > 0) {
            List<String> excessiveNames = priorityNutrients.stream()
                    .filter(n -> "초과".equals(n.getStatus()))
                    .map(WeeklyNutrientStatsDto::getNutrientName)
                    .collect(Collectors.toList());
            summary.append(String.join(", ", excessiveNames)).append(" 섭취가 과다합니다. ");
        }

        if (optimalCount > 0) {
            List<String> optimalNames = priorityNutrients.stream()
                    .filter(n -> "적정".equals(n.getStatus()))
                    .map(WeeklyNutrientStatsDto::getNutrientName)
                    .collect(Collectors.toList());
            summary.append(String.join(", ", optimalNames)).append("은(는) 적정 수준입니다.");
        }

        return summary.toString().trim();
    }

}
