package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NutrientAnalysisDto {
    private String name;
    private String status;
    private BigDecimal complianceRate;
    private String recommendation;
    private BigDecimal currentValue; // 현재 섭취량
    private BigDecimal minValue; // 권장 최소량
    private BigDecimal maxValue; // 권장 최대량
}
