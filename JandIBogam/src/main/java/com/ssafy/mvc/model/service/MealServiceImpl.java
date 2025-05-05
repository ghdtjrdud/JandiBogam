package com.ssafy.mvc.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mvc.model.dao.MealDao;
import com.ssafy.mvc.model.dto.MealDto;

@Service
@Transactional
public class MealServiceImpl implements MealService {

    private final MealDao mealDao;

    public MealServiceImpl(MealDao mealDao) {
        this.mealDao = mealDao;
    }


    @Override
    public MealDto createMeal(MealDto mealDto) {
        int result = mealDao.insertMeal(mealDto);
        if(result > 0){
            return mealDto;
        }
        return null;
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
