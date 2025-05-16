package com.ssafy.mvc.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eatDate;
    private String timeSlot; //enum 처리해야 돼
    private String photoUrl;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MealFoodDto> mealFoods;
    private List<String> foodNames;
    private List<FoodNutrientDto> foodNutrients;

}
