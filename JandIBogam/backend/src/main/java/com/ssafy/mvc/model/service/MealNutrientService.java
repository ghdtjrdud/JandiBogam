package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.*;
import com.ssafy.mvc.model.dto.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MealNutrientService {

    private final MealDao mealDao;
    private final MealFoodDao mealFoodDao;
    private final FoodNutrientDao foodNutrientDao;
    private final DailyNutrientSummaryDao dailyNutrientSummaryDao;
    private final NutrientDao nutrientDao;
    private final Map<String, Integer> nutrientNameIdMap = new ConcurrentHashMap<>();

    public MealNutrientService(MealDao mealDao, MealFoodDao mealFoodDao, FoodNutrientDao foodNutrientDao, DailyNutrientSummaryDao dailyNutrientSummaryDao, NutrientDao nutrientDao) {
        this.mealDao = mealDao;
        this.mealFoodDao = mealFoodDao;
        this.foodNutrientDao = foodNutrientDao;
        this.dailyNutrientSummaryDao = dailyNutrientSummaryDao;
        this.nutrientDao = nutrientDao;
    }

    @PostConstruct
    public void init() {
        List<NutrientIdNameDto> list = nutrientDao.findAllNameToIdMap();
        for(NutrientIdNameDto n : list) {
            nutrientNameIdMap.put(n.getName(), n.getId());
        }
    }
    //음식명으로 영양소 데이터 검색 메서드 추가
    public List<FoodNutrientDto> searchFoodNutrients(String foodName) {
        return foodNutrientDao.findByFoodName(foodName);
    }


    //사용자가 식단을 등록한 하루(특정 날짜)의 모든 식사에서 영양소 섭취량을 계산해서 저장
    @Transactional
    public void calculateDailyNutrients(int userId, LocalDate date) {
        //1.해당 날짜의 기존 영양소 합계 삭제 -> 재계산 (수정됐을 때)
        dailyNutrientSummaryDao.deleteByUserIdAndDate(userId, date);
        //2.해당 날짜의 사용자 식사 조회
        List<MealDto> meals = mealDao.selectByUserIdAndEatDate(userId, date);
        //3.영양소별 합계 계산
        Map<Integer, BigDecimal> nutrientTotals = calculateDailyNutrients(meals);
        // 4. 영양소 섭취량 저장 - 비어 있는지 확인 추가
        if (nutrientTotals != null && !nutrientTotals.isEmpty()) {
            for(Map.Entry<Integer, BigDecimal> entry : nutrientTotals.entrySet()) {
                DailyNutrientSummaryDto summary = new DailyNutrientSummaryDto();
                summary.setUserId(userId);
                summary.setSummaryDate(date);
                summary.setNutrientId(entry.getKey());
                summary.setTotalAmount(entry.getValue());

                System.out.println("일일 영양소 저장: " + summary); // 로깅 추가
                dailyNutrientSummaryDao.insert(summary);
            }
        } else {
            System.out.println("해당 날짜에 계산된 영양소 데이터가 없습니다: " + date);
        }
    }

    //여러 식사 기록에서 각 영양소별 총합 계산
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<Integer, BigDecimal> calculateDailyNutrients(List<MealDto> meals) {
        Map<Integer, BigDecimal> nutrientTotals = new HashMap<>();
        System.out.println("식사 목록 크기: " + (meals != null ? meals.size() : 0));

        if(meals != null && !meals.isEmpty()) {
            for (MealDto meal : meals) {
                List<MealFoodDto> mealFoods = mealFoodDao.selectByMealId(meal.getId());
                System.out.println("식사 ID: " + meal.getId() + ", 음식 개수: " + (mealFoods != null ? mealFoods.size() : 0));

                for (MealFoodDto mealFood : mealFoods) {
                    FoodNutrientDto foodNutrient = mealFood.getFoodNutrientDto();
                    if (foodNutrient != null) {
                        System.out.println("음식 처리: " + foodNutrient.getFoodName());
                        
                        int energyId = findNutrientIdByName("에너지");
                        int proteinId = findNutrientIdByName("단백질");
                        int fatId = findNutrientIdByName("지방");
                        int carbId = findNutrientIdByName("탄수화물");
                        int sugerId = findNutrientIdByName("당류");
                        int fiberId = findNutrientIdByName("식이섬유");
                        int sodiumId = findNutrientIdByName("나트륨");
                        int potassiumId = findNutrientIdByName("칼륨");
                        int cholesterolId = findNutrientIdByName("콜레스테롤");
                        int saturatedFatId = findNutrientIdByName("포화지방");

                        // 각 영양소 직접 추가
                        addNutrient(nutrientTotals, energyId, foodNutrient.getEnergy());
                        addNutrient(nutrientTotals, proteinId, foodNutrient.getProtein());
                        addNutrient(nutrientTotals, fatId, foodNutrient.getFat());
                        addNutrient(nutrientTotals, carbId, foodNutrient.getCarbohydrate());
                        addNutrient(nutrientTotals, sugerId, foodNutrient.getSugar());
                        addNutrient(nutrientTotals, fiberId, foodNutrient.getFiber());
                        addNutrient(nutrientTotals, sodiumId, foodNutrient.getSodium());
                        addNutrient(nutrientTotals, potassiumId, foodNutrient.getPotassium());
                        addNutrient(nutrientTotals, cholesterolId, foodNutrient.getCholesterol());
                        addNutrient(nutrientTotals, saturatedFatId, foodNutrient.getSaturatedFat());

                        System.out.println("영양소 추가 완료: " + foodNutrient.getFoodName());
                    }
                }
            }
        }

        System.out.println("계산된 영양소 개수: " + nutrientTotals.size() + ", 내용: " + nutrientTotals);
        return nutrientTotals;
    }

    private int findNutrientIdByName(String name) {
        return nutrientNameIdMap.getOrDefault(name, 0);
    }

    //영양소 값 더하기
    private void addNutrient(Map<Integer, BigDecimal> nutrientTotals, Integer nutrientId, Float amount) {
        if(nutrientId != null && amount != null) {
            BigDecimal currentAmount = nutrientTotals.getOrDefault(nutrientId, BigDecimal.ZERO);
            nutrientTotals.put(nutrientId, currentAmount.add(new BigDecimal(amount)));
        }
    }

    //사용자의 여러 일자에 대한 영양소 계산을 일괄 처리
    @Transactional
    public void calculateMealNutrientsForDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        while(!currentDate.isAfter(endDate)) {
            calculateDailyNutrients(userId, currentDate);
            currentDate = currentDate.plusDays(1);
        }
    }


}
