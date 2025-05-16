package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.FoodNutrientDto;

import java.util.List;

public interface FoodNutrientDao {
    List<FoodNutrientDto> findByFoodName(String foodName);
    FoodNutrientDto selectById(int id);
}
