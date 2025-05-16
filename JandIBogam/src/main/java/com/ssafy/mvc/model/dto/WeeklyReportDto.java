package com.ssafy.mvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    private List<WeeklyNutrientStatsDto> nutrients;


}
