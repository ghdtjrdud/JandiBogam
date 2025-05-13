package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dto.MealCommentDto;

import java.util.List;

public interface MealCommentService {
    int writeComment(MealCommentDto commentDto);

    List<MealCommentDto> getCommentsByMealId(int mealId);

    int updateComment(MealCommentDto mealCommentDto);

    int deleteComment(int id);
}
