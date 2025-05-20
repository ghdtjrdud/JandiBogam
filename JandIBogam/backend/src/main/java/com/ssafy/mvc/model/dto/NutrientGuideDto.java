package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NutrientGuideDto { //질병별 영양소 가이드라인을 병합한 결과 -> 비즈니스 로직 처리에 필요한 dto라서 따로 테이블 생성 X
    private int diseaseId;
    private int nutrientId;
    private BigDecimal min;
    private BigDecimal max;
    private boolean isRestriction;
    private String recommendation;

    public NutrientGuideDto() {}

    public NutrientGuideDto(int diseaseId,int nutrientId, BigDecimal min, BigDecimal max, boolean isRestriction,String recommendation) {
        this.diseaseId = diseaseId;
        this.nutrientId = nutrientId;
        this.min = min;
        this.max = max;
        this.isRestriction = isRestriction;
        this.recommendation = recommendation;
    }
}
