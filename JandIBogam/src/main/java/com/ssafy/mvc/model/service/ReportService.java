package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.*;
import com.ssafy.mvc.model.dto.NutrientGuideDto;
import com.ssafy.mvc.model.dto.WeeklyNutrientStatsDto;
import com.ssafy.mvc.model.dto.WeeklyReportDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class ReportService {
    private final WeeklyReportMapper weeklyReportMapper;
    private final WeeklyReportDao weeklyReportDao;
    private final UserDao userDao;
    private final NutrientDao nutrientDao;
    private final DiseaseService diseaseService;
    private final MealNutrientService mealNutrientService;

    public ReportService(WeeklyReportMapper weeklyReportMapper, WeeklyReportDao weeklyReportDao, UserDao userDao, NutrientDao nutrientDao, DiseaseService diseaseService, MealNutrientService mealNutrientService) {
        this.weeklyReportMapper = weeklyReportMapper;
        this.weeklyReportDao = weeklyReportDao;
        this.userDao = userDao;
        this.nutrientDao = nutrientDao;
        this.diseaseService = diseaseService;
        this.mealNutrientService = mealNutrientService;
    }

    // 주간 영양소 리포트 생성
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

        // 6. 리포트 생성
        WeeklyReportDto report = new WeeklyReportDto();
        report.setUserId(userId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setMealCount(mealCount);
        report.setNutrients(stats);
        report.setHealthScore(calculateHealthScore(stats));

        // 7. DB 저장
        weeklyReportDao.insert(report);
        return report;
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

    // 건강 점수 계산 (적정 영양소 비율)
    private Integer calculateHealthScore(List<WeeklyNutrientStatsDto> stats) {
        if (stats == null || stats.isEmpty()) return 0;

        long optimalCount = stats.stream()
                .filter(stat -> "적정".equals(stat.getStatus()))
                .count();

        return (int) ((optimalCount * 100.0) / stats.size());
    }

    // 준수율 계산 로직 개선
    private BigDecimal calculateCompliance(BigDecimal avg, NutrientGuideDto guide) {
        BigDecimal min = guide.getMin();
        BigDecimal max = guide.getMax();
        if (avg == null || min == null || max == null) {
            return BigDecimal.ZERO;
        }
        try {
            if (guide.isRestriction()) {
                if (avg.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return guide.getMax().multiply(BigDecimal.valueOf(100))
                        .divide(max, 2, RoundingMode.HALF_UP);
            } else {
                if (guide.getMin().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                return avg.multiply(BigDecimal.valueOf(100))
                        .divide(min, 2, RoundingMode.HALF_UP);
            }
        }catch(ArithmeticException e){
            return BigDecimal.ZERO;
        }
    }

    // 상태 판정
    private String calculateStatus(BigDecimal avg, NutrientGuideDto guide) {
        if (avg == null) {
            return "미확인";
        }
        if (guide.isRestriction()) {
            if(guide.getMax() == null) return "미확인";
            return avg.compareTo(guide.getMax()) > 0 ? "초과" : "적정";
        } else {
            if(guide.getMin() == null) return "미확인";
            return avg.compareTo(guide.getMin()) < 0 ? "부족" : "적정";
        }
    }
}
