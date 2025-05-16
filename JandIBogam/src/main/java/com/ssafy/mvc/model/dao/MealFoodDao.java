package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.MealFoodDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MealFoodDao {

     //식사 ID로 모든 식사-음식 매핑 정보 조회 (영양소 정보 포함)
    List<MealFoodDto> selectByMealId(int mealId);

    //식사-음식 매핑 정보 저장
    int insert(MealFoodDto mealFood);

    //여러 식사-음식 매핑 정보 일괄 저장
    int insertMultiple(@Param("mealId") int mealId, @Param("foodIds") List<Integer> foodIds);


    //식사 ID로 모든 식사-음식 매핑 정보 삭제
    int deleteByMealId(int mealId);
}