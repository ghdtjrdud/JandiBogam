package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyNutrientSummaryDto {
    private int id;
    private int userId;
    private LocalDate summaryDate;
    private int nutrientId;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String nutrientName;

    public DailyNutrientSummaryDto() {}

    public DailyNutrientSummaryDto(int userId, LocalDate summaryDate, int nutrientId, BigDecimal totalAmount) {
        this.userId = userId;
        this.summaryDate = summaryDate;
        this.nutrientId = nutrientId;
        this.totalAmount = totalAmount;
    }
}
