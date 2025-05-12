package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.MealCommentDto;
import com.ssafy.mvc.model.service.MealCommentService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meals")
public class MealCommentController {

    private final MealCommentService mealCommentService;

    @Autowired
    public MealCommentController(MealCommentService mealCommentService) {
        this.mealCommentService = mealCommentService;
    }

    @PostMapping("/{mealId}/comments")
    public ResponseEntity<?> writeComment(@PathVariable int mealId, @RequestBody MealCommentDto commentDto) {

        commentDto.setMealId(mealId);
        int result = mealCommentService.writeComment(commentDto);

        if (result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 작성 실패");
        }


    }

}
