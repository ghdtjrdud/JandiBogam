package com.ssafy.mvc.model.dao;

import java.time.LocalDate;
import java.util.List;

import com.ssafy.mvc.model.dto.MealDto;

public interface MealDao {

    //식단 등록
    int insertMeal(MealDto mealDto);

    //식단 조회 (식단 기록 id)
    MealDto selectById(int id);

    //식단 조회 (기간 + 시간대)
    List<MealDto> selectMealsByFilter(int userId, LocalDate startDate, LocalDate endDate, String timeSlot);

    //식단 수정
    int updateMeal(MealDto mealDto);

    //식단 삭제
    int deleteMeal(int id);

}
