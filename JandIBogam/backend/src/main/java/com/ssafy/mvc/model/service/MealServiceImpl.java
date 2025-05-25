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

    // 1. 식단 생성 (핵심 로직) - 개선된 버전
    @Override
    public MealDto createMeal(MealDto mealDto) {
        try {
            logger.info("식단 등록 시작: 사용자={}, 날짜={}, 시간대={}",
                    mealDto.getUserId(), mealDto.getEatDate(), mealDto.getTimeSlot());

            int result = mealDao.insertMeal(mealDto);
            if (result <= 0) {
                logger.error("식단 기본 정보 저장 실패");
                return null;
            }

            // 음식 이름 → ID 변환 로직
            if (mealDto.getFoodNames() != null && !mealDto.getFoodNames().isEmpty()) {
                List<Integer> foodIds = new ArrayList<>();
                List<String> notFoundFoods = new ArrayList<>();

                for (String foodName : mealDto.getFoodNames()) {
                    // 음식 이름으로 DB에서 검색
                    List<FoodNutrientDto> foundFoods = mealNutrientService.searchFoodNutrients(foodName);

                    if (!foundFoods.isEmpty()) {
                        foodIds.add(foundFoods.get(0).getId());
                        logger.debug("음식 검색 성공: {} -> ID: {}", foodName, foundFoods.get(0).getId());
                    } else {
                        notFoundFoods.add(foodName);
                        logger.warn("음식 검색 실패: {}", foodName);
                    }
                }

                if (!foodIds.isEmpty()) {
                    mealDao.insertMealFoods(mealDto.getId(), foodIds);
                    logger.info("식단-음식 연결 정보 저장 완료: 식단ID={}, 저장된 음식 수={}",
                            mealDto.getId(), foodIds.size());
                }

                // 검색 실패한 음식이 있으면 경고 로그
                if (!notFoundFoods.isEmpty()) {
                    logger.error("다음 음식들을 데이터베이스에서 찾을 수 없습니다: {}", notFoundFoods);
                }
            }

            // 영양소 계산
            mealNutrientService.calculateDailyNutrients(mealDto.getUserId(), mealDto.getEatDate());

            return getMeal(mealDto.getId()); // 저장된 데이터를 다시 조회하여 반환
        } catch (Exception e) {
            logger.error("식단 생성 중 오류 발생", e);
            return null;
        }
    }

    // 2. 식단 조회 (개선된 버전)
    @Override
    public MealDto getMeal(int id) {
        MealDto mealDto = mealDao.selectById(id);
        if (mealDto != null) {
            // 음식 ID를 이름으로 변환
            List<Integer> foodIds = mealDao.selectMealFoodById(id);
            List<String> foodNames = new ArrayList<>();

            for (Integer foodId : foodIds) {
                FoodNutrientDto food = foodNutrientDao.selectById(foodId);
                if (food != null) {
                    foodNames.add(food.getFoodName());
                    logger.debug("음식 ID {} -> 이름: {}", foodId, food.getFoodName());
                } else {
                    logger.warn("음식 ID {}에 해당하는 데이터를 찾을 수 없습니다", foodId);
                    foodNames.add("알 수 없는 음식 (ID: " + foodId + ")");
                }
            }

            mealDto.setFoodNames(foodNames);
            logger.info("식단 조회 완료: ID={}, 음식 수={}", id, foodNames.size());
        }
        return mealDto;
    }

    // 3. 식단 필터 조회
    @Override
    public List<MealDto> getMealsByFilter(int userId, LocalDate startDate, LocalDate endDate, String timeSlot) {
        return mealDao.selectMealsByFilter(userId, startDate, endDate, timeSlot);
    }

    // 4. 식단 수정 - 개선된 버전
    @Override
    public MealDto updateMeal(int id, int userId, MealDto mealDto) {
        try {
            logger.info("식단 수정 시작: ID={}, 사용자={}, 전달받은 음식 개수={}",
                    id, userId, mealDto.getFoodNames() != null ? mealDto.getFoodNames().size() : 0);

            // 전달받은 foodNames 로그 출력
            if (mealDto.getFoodNames() != null) {
                logger.info("전달받은 음식 목록: {}", mealDto.getFoodNames());
            }

            MealDto existingMeal = mealDao.selectById(id);
            if (existingMeal == null || existingMeal.getUserId() != userId) {
                logger.error("식단을 찾을 수 없거나 권한이 없습니다: ID={}, 사용자={}", id, userId);
                return null;
            }

            // 기존 날짜 저장 (영양소 재계산용)
            LocalDate originalDate = existingMeal.getEatDate();

            mealDto.setId(id);
            mealDto.setUserId(userId);

            // 식단 기본 정보 업데이트
            int updateResult = mealDao.updateMeal(mealDto);
            if (updateResult <= 0) {
                logger.error("식단 기본 정보 업데이트 실패: ID={}", id);
                return null;
            }
            logger.info("식단 기본 정보 업데이트 완료: ID={}", id);

            // 기존 meal_foods 삭제
            int deleteResult = mealDao.deleteMealFoods(id);
            logger.info("기존 음식 연결 정보 삭제: ID={}, 삭제된 행 수={}", id, deleteResult);

            // 새로운 음식 정보 처리 - 개선된 로직
            if (mealDto.getFoodNames() != null && !mealDto.getFoodNames().isEmpty()) {
                logger.info("새로운 음식 정보 처리 시작: 음식 개수={}", mealDto.getFoodNames().size());

                List<Integer> foodIds = new ArrayList<>();
                List<String> notFoundFoods = new ArrayList<>();
                List<String> successFoods = new ArrayList<>();

                for (String foodName : mealDto.getFoodNames()) {
                    if (foodName == null || foodName.trim().isEmpty()) {
                        logger.warn("빈 음식 이름이 전달됨, 건너뜀");
                        continue;
                    }

                    String trimmedFoodName = foodName.trim();
                    logger.debug("음식 검색 시도: '{}'", trimmedFoodName);

                    try {
                        List<FoodNutrientDto> foundFoods = mealNutrientService.searchFoodNutrients(trimmedFoodName);
                        if (!foundFoods.isEmpty()) {
                            foodIds.add(foundFoods.get(0).getId());
                            successFoods.add(trimmedFoodName);
                            logger.info("음식 검색 성공: '{}' -> ID: {}", trimmedFoodName, foundFoods.get(0).getId());
                        } else {
                            notFoundFoods.add(trimmedFoodName);
                            logger.warn("음식 검색 실패 (결과 없음): '{}'", trimmedFoodName);
                        }
                    } catch (Exception e) {
                        logger.error("음식 검색 중 예외 발생: '{}', 오류: {}", trimmedFoodName, e.getMessage());
                        notFoundFoods.add(trimmedFoodName);
                    }
                }

                // 검색된 음식이 있으면 저장
                if (!foodIds.isEmpty()) {
                    try {
                        mealDao.insertMealFoods(id, foodIds);
                        logger.info("식단-음식 연결 정보 저장 완료: 식단ID={}, 저장된 음식 수={}, 성공한 음식: {}",
                                id, foodIds.size(), successFoods);
                    } catch (Exception e) {
                        logger.error("식단-음식 연결 정보 저장 실패: 식단ID={}, 오류: {}", id, e.getMessage(), e);
                        throw e; // 트랜잭션 롤백을 위해 예외 재발생
                    }
                } else {
                    logger.warn("저장할 유효한 음식이 없습니다: 식단ID={}, 전달받은 음식: {}", id, mealDto.getFoodNames());
                }

                // 검색 실패한 음식들이 있으면 로그로 알림
                if (!notFoundFoods.isEmpty()) {
                    logger.error("다음 음식들을 데이터베이스에서 찾을 수 없습니다: {}", notFoundFoods);
                }
            } else {
                logger.info("수정할 음식 정보가 없습니다: 식단ID={}, foodNames={}", id, mealDto.getFoodNames());
            }

            // 영양소 계산 업데이트
            try {
                // 기존 날짜의 영양소 재계산
                mealNutrientService.calculateDailyNutrients(userId, originalDate);
                logger.info("기존 날짜 영양소 재계산 완료: {}", originalDate);

                // 수정된 날짜가 다르면 새 날짜도 계산
                if (!originalDate.equals(mealDto.getEatDate())) {
                    mealNutrientService.calculateDailyNutrients(userId, mealDto.getEatDate());
                    logger.info("새 날짜 영양소 계산 완료: {}", mealDto.getEatDate());
                }
            } catch (Exception e) {
                logger.error("영양소 계산 중 오류 발생", e);
                // 영양소 계산 실패는 식단 수정 자체를 실패시키지 않음
            }

            // 수정된 데이터 조회하여 반환
            MealDto updatedMeal = getMeal(id);
            logger.info("식단 수정 완료: ID={}, 최종 음식 수={}", id,
                    updatedMeal != null && updatedMeal.getFoodNames() != null ? updatedMeal.getFoodNames().size() : 0);

            return updatedMeal;

        } catch (Exception e) {
            logger.error("식단 수정 중 오류 발생: ID={}", id, e);
            throw e; // 트랜잭션 롤백을 위해 예외 재발생
        }
    }

    // 5. 식단 삭제
    @Override
    @Transactional
    public boolean deleteMeal(int id, int userId) {
        try {
            // 영양소 재계산을 위해 삭제 전 식단 정보 조회
            MealDto existingMeal = mealDao.selectById(id);

            // 1. meal_foods에서 먼저 삭제
            mealFoodDao.deleteByMealId(id);

            // 2. meals에서 삭제
            int result = mealDao.deleteMeal(id, userId);

            // 3. 성공적으로 삭제되었으면 영양소 재계산
            if (result > 0 && existingMeal != null) {
                try {
                    mealNutrientService.calculateDailyNutrients(userId, existingMeal.getEatDate());
                    logger.info("식단 삭제 후 영양소 재계산 완료: 날짜={}", existingMeal.getEatDate());
                } catch (Exception e) {
                    logger.error("식단 삭제 후 영양소 재계산 실패", e);
                }
            }

            return result > 0;
        } catch (Exception e) {
            logger.error("식단 삭제 중 오류 발생: ID={}", id, e);
            throw e;
        }
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