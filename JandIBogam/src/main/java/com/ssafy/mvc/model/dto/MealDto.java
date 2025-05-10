package com.ssafy.mvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealDto {
    private int id;
    private int userId;
    private LocalDate eatDate;
    private String timeSlot; //enum 처리해야 돼
    private String photoUrl;
    private List<Integer> foodIds;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
