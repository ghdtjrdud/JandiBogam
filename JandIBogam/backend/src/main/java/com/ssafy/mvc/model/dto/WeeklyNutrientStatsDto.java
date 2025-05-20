package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeeklyNutrientStatsDto { //주간 영양소 통계를 담는 객체로 필요한 dto
    private int nutrientId;
    private String nutrientName;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private String status; //부족, 적정, 초과
    private BigDecimal complianceRate;
    private String recommendation;
}
