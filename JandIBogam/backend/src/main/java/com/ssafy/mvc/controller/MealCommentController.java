package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dao.MealCommentDao;
import com.ssafy.mvc.model.dto.MealCommentDto;
import com.ssafy.mvc.model.service.MealCommentService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
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

    @GetMapping("/{mealId}/comments")
    public ResponseEntity<?> getComments(@PathVariable int mealId) {
        List<MealCommentDto> commentList = mealCommentService.getCommentsByMealId(mealId);

        if (commentList != null && !commentList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(commentList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("아직 댓글이 없습니다.");
        }
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable int id, @RequestBody MealCommentDto mealCommentDto) {

        mealCommentDto.setId(id);
        int result = mealCommentService.updateComment(mealCommentDto);

        if (result > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("댓글 수정 성공");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("수정할 댓글이 없습니다.");
        }

    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable int id) {

        int result = mealCommentService.deleteComment(id);

        if (result > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("삭제할 댓글이 없습니다.");
        }

    }

}
