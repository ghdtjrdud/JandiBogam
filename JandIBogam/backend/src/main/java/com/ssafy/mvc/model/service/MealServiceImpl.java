package com.ssafy.mvc.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.ssafy.mvc.model.dao.FoodNutrientDao;
import com.ssafy.mvc.model.dao.MealFoodDao;
import com.ssafy.mvc.model.dto.FoodNutrientDto;
import com.ssafy.mvc.model.dto.NutrientDto;
import com.ssafy.mvc.model.dto.NutrientIdNameDto;
import jakarta.annotation.PostConstruct;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ssafy.mvc.model.dao.MealDao;
import com.ssafy.mvc.model.dao.NutrientDao;
import com.ssafy.mvc.model.dto.MealDto;

@Service
@Transactional
public class MealServiceImpl implements MealService {
    private static final Logger logger = LoggerFactory.getLogger(MealServiceImpl.class);

    private final MealDao mealDao;
    private final MealNutrientService mealNutrientService;
    private final NutrientDao nutrientDao;
    private final FoodNutrientDao foodNutrientDao;
    private final Map<String, Integer> nutrientNameIdMap = new ConcurrentHashMap<>();
    private final MealFoodDao mealFoodDao;

    @Autowired
    public MealServiceImpl(MealDao mealDao,
                           MealNutrientService mealNutrientService,
                           NutrientDao nutrientDao, FoodNutrientDao foodNutrientDao, MealFoodDao mealFoodDao) {
        this.mealDao = mealDao;
        this.mealNutrientService = mealNutrientService;
        this.nutrientDao = nutrientDao;
        this.foodNutrientDao = foodNutrientDao;
        this.mealFoodDao = mealFoodDao;
    }

    @PostConstruct
    public void init() {
        List<NutrientIdNameDto> nutrientList = nutrientDao.findAllNameToIdMap();
        for(NutrientIdNameDto nutrient : nutrientList) {
            nutrientNameIdMap.put(nutrient.getName(), nutrient.getId());
        }
    }

    // 1. 식단 생성 (핵심 로직)
    @Override
    public MealDto createMeal(MealDto mealDto) {
        try {
            logger.info("식단 등록 시작: 사용자={}, 날짜={}, 시간대={}",
                    mealDto.getUserId(), mealDto.getEatDate(), mealDto.getTimeSlot());
            int result = mealDao.insertMeal(mealDto);
            if (result <= 0) {
                return null;
            }

            // 음식 이름 → ID 변환 로직
            if (mealDto.getFoodNames() != null && !mealDto.getFoodNames().isEmpty()) {
                List<Integer> foodIds = new ArrayList<>();

                for (String foodName : mealDto.getFoodNames()) {
                    //음식 이름으로 DB에서 검색
                    List<FoodNutrientDto> foundFoods = mealNutrientService.searchFoodNutrients(foodName);
                    //검색 결과 있으면 첫번째 결과 id 사용 -> 유사 음식 처리하려고
                    if (!foundFoods.isEmpty()) {
                        foodIds.add(foundFoods.get(0).getId());
                    }
                }
                if (!foodIds.isEmpty()) {
                    mealDao.insertMealFoods(mealDto.getId(), foodIds);
                    logger.debug("식단-음식 연결 정보 저장 완료: 식단ID={}, 음식 수={}",
                            mealDto.getId(), foodIds.size());
                }
            }

            mealNutrientService.calculateDailyNutrients(mealDto.getUserId(), mealDto.getEatDate());
            return mealDto;
        } catch (Exception e) {
            logger.error("식단 생성 중 오류 발생",e);
            return null;
        }
    }

    // 2. 식단 조회
    @Override
    public MealDto getMeal(int id) {
        MealDto mealDto = mealDao.selectById(id);
        if (mealDto != null) {
            // 음식 ID를 이름으로 변환
            List<String> foodNames = mealDao.selectMealFoodById(id).stream()
                    .map(foodId -> {
                        FoodNutrientDto food = foodNutrientDao.selectById(foodId);
                        return food != null ? food.getFoodName() : "알 수 없는 음식";
                    })
                    .collect(Collectors.toList());
            mealDto.setFoodNames(foodNames);
        }
        return mealDto;
    }


    // 3. 식단 필터 조회
    @Override
    public List<MealDto> getMealsByFilter(int userId, LocalDate startDate, LocalDate endDate, String timeSlot) {
        return mealDao.selectMealsByFilter(userId, startDate, endDate, timeSlot);
    }

    // 4. 식단 수정
    @Override
    public MealDto updateMeal(int id, int userId, MealDto mealDto) {
        MealDto existingMeal = mealDao.selectById(id);
        if (existingMeal == null || existingMeal.getUserId() != userId) {
            return null;
        }

        mealDto.setId(id);
        mealDto.setUserId(userId);

        int r1 = mealDao.updateMeal(mealDto);
        if (r1 > 0) {
            mealDao.deleteMealFoods(id);

            if (mealDto.getFoodNames() != null && !mealDto.getFoodNames().isEmpty()) {
                List<Integer> foodIds = new ArrayList<>();
                for(String foodName : mealDto.getFoodNames()){
                    List<FoodNutrientDto> foundFoods = mealNutrientService.searchFoodNutrients(foodName);
                    if(!foundFoods.isEmpty()){
                        foodIds.add(foundFoods.get(0).getId());
                    }else{
                        logger.warn("음식 검색 실패: {}", foodName);
                    }
                }
                if(!foodIds.isEmpty()) {
                    mealDao.insertMealFoods(mealDto.getId(), foodIds);
                }
            }

            mealNutrientService.calculateDailyNutrients(mealDto.getUserId(), existingMeal.getEatDate());
            if (!existingMeal.getEatDate().equals(mealDto.getEatDate())) {
                mealNutrientService.calculateDailyNutrients(mealDto.getUserId(), mealDto.getEatDate());
            }
            return mealDao.selectById(mealDto.getId());
        }
        return null;
    }

    // 5. 식단 삭제
    @Override
    public boolean deleteMeal(int id, int userId) {
        MealDto meal = mealDao.selectById(id);
        if (meal == null) return false;

        // 1. meal_foods에서 먼저 삭제
        mealFoodDao.deleteByMealId(id);

        int result = mealDao.deleteMeal(id, userId);
        if (result > 0) {
            mealNutrientService.calculateDailyNutrients(meal.getUserId(), meal.getEatDate());
            return true;
        }
        return false;
    }

    // 6. 음식 검색 기반 식단 생성
    @Override
    public MealDto createMealWithFoodSearch(MealDto mealDto) {
        try {
            logger.info("음식 검색 기반 식단 생성 시작: 사용자={}, 음식 개수={}",
                    mealDto.getUserId(), mealDto.getFoodNames() != null ? mealDto.getFoodNames().size() : 0);

            // 음식 이름이 있는 경우만 처리
            List<FoodNutrientDto> nutrients = new ArrayList<>();
            if (mealDto.getFoodNames() != null && !mealDto.getFoodNames().isEmpty()) {
                for (String foodName : mealDto.getFoodNames()) {
                    // 각 음식 이름으로 검색
                    List<FoodNutrientDto> found = mealNutrientService.searchFoodNutrients(foodName);
                    if (!found.isEmpty()) {
                        // 각 음식 이름당 첫 번째 검색 결과만 사용
                        nutrients.add(found.get(0));
                        logger.debug("음식 검색 결과 추가: 이름={}, ID={}",
                                found.get(0).getFoodName(), found.get(0).getId());
                    } else {
                        logger.warn("음식 검색 결과 없음: 이름={}", foodName);
                    }
                }
            }

            // 식단 DTO에 영양소 정보 설정 후 식단 생성 메서드 호출
            mealDto.setFoodNutrients(nutrients);
            logger.debug("식단에 음식 영양소 정보 설정 완료: {} 항목", nutrients.size());

            return createMeal(mealDto);
        } catch (Exception e) {
            logger.error("음식 검색 기반 식단 생성 중 오류 발생", e);
            return null;
        }
    }
}
