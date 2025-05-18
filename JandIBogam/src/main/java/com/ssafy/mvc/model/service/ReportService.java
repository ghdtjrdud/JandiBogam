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
import java.util.List;
import java.util.Map;

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

        // 6. 리포트 DTO 조립
        WeeklyReportDto report = WeeklyReportDto.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .mealCount(mealCount)
                .nutrients(stats)
                .healthScore(calculateHealthScore(stats))
                .build();

        // — AI 호출 & 추천 세팅 —
        String prompt = buildforgpt(report);
        String recommendation = openAiService.recommendDiet(prompt);
        report.setRecommendation(recommendation);

        // 7. DB 저장
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

    // GPT 프롬프트용 문자열 생성
    private String buildforgpt(WeeklyReportDto report) {
        StringBuilder sb = new StringBuilder();
        sb.append("사용자 ").append(report.getUserId())
                .append("님의 주간 리포트 (")
                .append(report.getStartDate()).append(" ~ ")
                .append(report.getEndDate()).append(")\n");
        sb.append("식사 횟수: ").append(report.getMealCount()).append("\n");
        sb.append("건강 점수: ").append(report.getHealthScore()).append("%\n");
        sb.append("영양소별 통계:\n");
        for (WeeklyNutrientStatsDto stat : report.getNutrients()) {
            sb.append("- ").append(stat.getNutrientName())
                    .append(": 평균 ").append(stat.getAvg())
                    .append(", 상태 ").append(stat.getStatus()).append("\n");
        }
        return sb.toString();
    }
}
