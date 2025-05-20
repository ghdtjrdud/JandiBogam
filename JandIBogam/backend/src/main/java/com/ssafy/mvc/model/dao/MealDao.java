package com.ssafy.mvc.model.dao;

import java.time.LocalDate;
import java.util.List;

import com.ssafy.mvc.model.dto.MealDto;
import org.springframework.data.repository.query.Param;

public interface MealDao {

    //식단 등록
    int insertMeal(MealDto mealDto);
    
    //식단에 대응하는 음식 등록
    void insertMealFoods(@Param("mealId") int mealId, @Param("foodIds") List<Integer> foodIds);

    //식단 조회 (식단 기록 id)
    MealDto selectById(int id);

    //식단 상세조회 시 음식 목록 조회
    List<Integer> selectMealFoodById(int id);

    // 특정 날짜의 사용자 식사 목록 조회 - 영양소 계산에 필요
    List<MealDto> selectByUserIdAndEatDate(
            @Param("userId") int userId,
            @Param("eatDate") LocalDate eatDate
    );

    //기간 내 사용자 식사 목록 조회 (기간 + 시간대)
    List<MealDto> selectMealsByFilter(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("timeSlot") String timeSlot);

    // 기간 내 사용자 식사 목록 조회
    List<MealDto> selectByUserIdBetweenDates(
            @Param("userId") int userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    // 기간 내 식사 개수 조회
    int countByUserIdBetweenDates(
            @Param("userId") int userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    //식단 수정
    int updateMeal(MealDto mealDto);

    //식단 삭제
    int deleteMeal(@Param("id") int id, @Param("userId") int userId);

    // mealfoods 삭제(수정 시에도 필요함) -> 식단에 연결된 음식 정보 삭제
    int deleteMealFoods(int id);
}
