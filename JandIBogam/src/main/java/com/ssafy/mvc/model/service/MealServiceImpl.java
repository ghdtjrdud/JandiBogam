package com.ssafy.mvc.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mvc.model.dao.MealDao;
import com.ssafy.mvc.model.dto.MealDto;

@Service
@Transactional
public class MealServiceImpl implements MealService {

    private final MealDao mealDao;

    @Autowired
    public MealServiceImpl(MealDao mealDao) {
        this.mealDao = mealDao;
    }


    @Override
    public MealDto createMeal(MealDto mealDto) {
        int r1 = mealDao.insertMeal(mealDto);
        if(r1 <= 0){
            return null;
        }

        if (mealDto.getFoodIds() != null && !mealDto.getFoodIds().isEmpty()) {
            int r2 = mealDao.insertMealFoods(mealDto);
            if (r2 <= 0) {
                System.out.println("음식이 입력되지 않았습니다.");
            }
        }
        return mealDto;
    }

    @Override
    public MealDto getMeal(int id) {
        MealDto mealDto = mealDao.selectById(id);
        if (mealDto != null) {
            List<Integer> foodIds = mealDao.selectmealFoodByid(id);
            mealDto.setFoodIds(foodIds);
        }
        return mealDto;
    }

    @Override
    public List<MealDto> getMealsByFilter(int userId, LocalDate startDate, LocalDate endDate, String timeSlot) {
        return mealDao.selectMealsByFilter(userId, startDate, endDate, timeSlot);
    }


    @Override
    public MealDto updateMeal(MealDto mealDto) {
        int r1 = mealDao.updateMeal(mealDto);
        if (r1 <= 0) {
            System.out.println("식단 수정 실패: meals 테이블 업데이트 실패");
            return null;
        }

        int r2 = mealDao.deleteMealFoods(mealDto.getId());
        System.out.println("기존 음식 연결 삭제 수: " + r2);

        if (mealDto.getFoodIds() != null && !mealDto.getFoodIds().isEmpty()) {
            int r3 = mealDao.insertMealFoods(mealDto);
            if (r3 <= 0) {
                System.out.println("수정 중 음식 목록 등록 실패");
            } else {
                System.out.println("음식 등록 완료: " + r3 + "건");
            }
        }

        return mealDto;
    }


    @Override
    public boolean deleteMeal(int id) {
        return mealDao.deleteMeal(id) > 0;
    }
}
