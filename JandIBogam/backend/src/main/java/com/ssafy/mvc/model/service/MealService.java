package com.ssafy.mvc.model.service;

import java.time.LocalDate;
import java.util.List;

import com.ssafy.mvc.model.dto.MealDto;


public interface MealService {

    //식단 등록
    MealDto createMeal(MealDto mealDto);

    //식단 조회(1개)
    MealDto getMeal(int id);

    //식단 조회(필터링)
    List<MealDto> getMealsByFilter(int userId, LocalDate startDate, LocalDate endDate, String timeSlot);

    //식단 수정
    MealDto updateMeal(int id, int userId, MealDto mealDto);

    //식단 삭제
    boolean deleteMeal(int id, int userId);

    MealDto createMealWithFoodSearch(MealDto mealDto);


}