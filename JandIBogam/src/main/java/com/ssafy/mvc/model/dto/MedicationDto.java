package com.ssafy.mvc.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicationDto {
    private int id;
    private int userId;
    private LocalDate medDate; //복용 날짜
    private String timeSlot; //복용 시간대 (아/점/저 선택)
    private String drugName; //약 이름
    private String drugType; //약 종류
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
