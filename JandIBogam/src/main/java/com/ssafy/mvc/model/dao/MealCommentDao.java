package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.MealCommentDto;

import java.util.List;

public interface MealCommentDao {
    int insertComment(MealCommentDto commentDto);

    List<MealCommentDto> selectCommentById(int mealId);

    int updateComment(MealCommentDto mealCommentDto);

    int deleteComment(int id);
}
