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
        return mealDao.selectById(id);
    }

    @Override
    public List<MealDto> getMealsByFilter(int userId, LocalDate startDate, LocalDate endDate, String timeSlot) {
        return mealDao.selectMealsByFilter(userId, startDate, endDate, timeSlot);
    }


    @Override
    public MealDto updateMeal(MealDto mealDto) {
        int result = mealDao.updateMeal(mealDto);
        if(result > 0){
            return mealDto;
        }
        return null;
    }

    @Override
    public boolean deleteMeal(int id) {
        return mealDao.deleteMeal(id) > 0;
    }
}
