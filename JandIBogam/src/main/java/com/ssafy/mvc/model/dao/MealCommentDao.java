package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.MealCommentDto;

public interface MealCommentDao {
    int insertComment(MealCommentDto commentDto);
}
