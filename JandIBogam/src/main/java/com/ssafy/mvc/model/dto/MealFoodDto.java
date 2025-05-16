package com.ssafy.mvc.model.dto;

import lombok.Data;

@Data
public class MealFoodDto {
    private int mealId;
    private int foodId;
    private FoodNutrientDto foodNutrientDto;
}
