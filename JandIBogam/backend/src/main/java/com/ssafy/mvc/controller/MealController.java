package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dao.MealDao;
import com.ssafy.mvc.model.dto.MealDto;
import com.ssafy.mvc.model.service.MealNutrientService;
import com.ssafy.mvc.model.service.MealService;
import com.ssafy.mvc.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/meals")
public class MealController {


    private final MealDao mealDao;
    private final MealNutrientService mealNutrientService;
    private final MealService mealService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MealController(MealDao mealDao, MealNutrientService mealNutrientService, MealService mealService, JwtTokenProvider jwtTokenProvider) {
        this.mealDao = mealDao;
        this.mealNutrientService = mealNutrientService;
        this.mealService = mealService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //등록
    @PostMapping
    public ResponseEntity<?> createMeal(@RequestBody MealDto mealDto, HttpServletRequest request) {
        try {
            // 1단계: JWT 토큰 추출 및 사용자 ID 획득
            try {
                int userId = jwtTokenProvider.extractUserId(request);
                System.out.println("JWT에서 추출한 사용자 ID: " + userId); // 추출된 ID 로깅
                mealDto.setUserId(userId);
            } catch (Exception e) {
                System.err.println("JWT에서 사용자 ID 추출 중 오류: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("JWT 인증 실패: " + e.getMessage());
            }

            // 2단계: 식단 생성 서비스 호출
            try {
                MealDto created = mealService.createMeal(mealDto);
                if (created == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식단 생성 실패: 서비스에서 null 반환");
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            } catch (Exception e) {
                System.err.println("식단 생성 서비스 호출 중 오류: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("식단 생성 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            // 최상위 예외 처리 (예상치 못한 오류)
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류: " + e.getMessage());
        }
    }

    //조회 - 상세
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeal(@PathVariable int id) {
        MealDto mealDto = mealService.getMeal(id);
        return (mealDto != null) ? ResponseEntity.ok(mealDto) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal not found");
    }

    //조회 - 필터링
// (1) userId를 파라미터로 받는다!
    @GetMapping("/filter")
    public ResponseEntity<?> getMealsByFilter(
            @RequestParam int userId, // 쿼리에서 userId 받아오기!
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "전체") String timeSlot
    ) {
        // (2) 받은 userId로 그대로 조회!
        List<MealDto> mealDtos = mealDao.selectByUserIdBetweenDates(userId, startDate, endDate);
        if (!"전체".equals(timeSlot)) {
            mealDtos = mealDtos.stream()
                    .filter(meal -> timeSlot.equals(meal.getTimeSlot()))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.status(HttpStatus.OK).body(mealDtos);
    }


    //수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable int id, @RequestBody MealDto mealDto, HttpServletRequest request) {
        int userId = jwtTokenProvider.extractUserId(request);
        mealDto.setId(id);
        MealDto updated = mealService.updateMeal(id, userId, mealDto);
        if (updated != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal not found");
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable int id, HttpServletRequest request) {
        int userId = jwtTokenProvider.extractUserId(request);
        boolean deleted = mealService.deleteMeal(id, userId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body("deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("failed to delete");
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<?> getUserMealsByDate(HttpServletRequest request, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        int userId = jwtTokenProvider.extractUserId(request);
        List<MealDto> meals = mealDao.selectByUserIdAndEatDate(userId, date);
        return ResponseEntity.status(HttpStatus.OK).body(meals);
    }

    //영양소 섭취 - 하루
    @PostMapping("/nutrients/daily/{date}")
    public ResponseEntity<?> calculateDailyNutrients(HttpServletRequest request,
                                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            int userId = jwtTokenProvider.extractUserId(request);
            mealNutrientService.calculateDailyNutrients(userId, date);
            return ResponseEntity.status(HttpStatus.OK).body("calculated daily nutrients");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("영양소 계산 중 오류 발생: " + e.getMessage());
        }
    }

    //영양소 섭취 - 일주일(일정 기간)
    @PostMapping("/nutrients/weekly")
    public ResponseEntity<?> calculateWeeklyNutrients(HttpServletRequest request, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        int userId = jwtTokenProvider.extractUserId(request);
        mealNutrientService.calculateMealNutrientsForDateRange(userId, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body("calculated weekly nutrients");
    }

    @PostMapping("/with-search")
    public ResponseEntity<?> createMealWithFoodSearch(
            @RequestBody MealDto mealDto, // foodNames 필드 포함
            HttpServletRequest request
    ) {
        try {
            int userId = jwtTokenProvider.extractUserId(request);
            mealDto.setUserId(userId);

            // 필수 필드 검증 추가
            if (mealDto.getFoodNames() == null || mealDto.getFoodNames().isEmpty()) {
                return ResponseEntity.badRequest().body("음식 이름을 입력해주세요.");
            }

            MealDto created = mealService.createMealWithFoodSearch(mealDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (JwtException e) {
            return ResponseEntity.status(401).body("인증 실패");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류");
        }
    }


}