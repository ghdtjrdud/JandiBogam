package com.ssafy.mvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportDto {
    private int id;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int mealCount;
    private Integer healthScore;
    private LocalDateTime createdAt;
    private String recommendation;

    // AI 관련 필드들 (DB 저장)
    private String aiRecommendation;  // AI 식단 추천 (DB 컬럼 필요)
    private LocalDateTime aiGeneratedAt;  // AI 생성 시간 (DB 컬럼 필요)
    private String healthTips; // AI 건강 조언 (DB 컬럼 필요)

    private List<WeeklyNutrientStatsDto> nutrients;

    // 계산으로 생성되는 필드들 (DB 저장 불필요)
    private List<WeeklyNutrientStatsDto> priorityNutrients; // 계산으로 생성
    private String nutrientSummary; // 로직으로 생성
}