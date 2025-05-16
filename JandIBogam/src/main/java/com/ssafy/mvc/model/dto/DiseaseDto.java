package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiseaseDto {
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
