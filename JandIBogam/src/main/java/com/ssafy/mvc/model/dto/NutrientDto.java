package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NutrientDto {
    private int id;
    private String name;
    private String unit;
    private String description;
    private LocalDateTime createdAt;
}
