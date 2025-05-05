package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.MealDto;
import com.ssafy.mvc.model.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/meals")
//@RequiredArgsConstructor
public class MealController {



    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    //등록
    @PostMapping
    public ResponseEntity<?> createMeal(@RequestBody MealDto mealDto){
        MealDto created = mealService.createMeal(mealDto);
        return created != null ? ResponseEntity.status(HttpStatus.CREATED).body(created)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot create Meal");
    }

    //조회 - 상세
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeal(@PathVariable int id){
        MealDto mealDto = mealService.getMeal(id);
        return (mealDto != null) ? ResponseEntity.ok(mealDto) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal not found");
    }

    //조회 - 필터링
    @GetMapping("/users/{userId}/filter")
    public ResponseEntity<?> getMealsByFilter(@PathVariable int userId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                              @RequestParam(defaultValue = "전체") String timeSlot
    ){
        List<MealDto> mealDtos = mealService.getMealsByFilter(userId, startDate, endDate, timeSlot);
        return ResponseEntity.status(HttpStatus.OK).body(mealDtos);
    }

    //수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable int id, @RequestBody MealDto mealDto){
        mealDto.setId(id);
        MealDto updated = mealService.updateMeal(mealDto);
        if(updated != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meal not found");
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable int id){
        boolean deleted = mealService.deleteMeal(id);
        if(deleted){
            return ResponseEntity.status(HttpStatus.OK).body("deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("failed to delete");
    }

    //사진 업로드 추가해야 됨 (@PostMapping)

}