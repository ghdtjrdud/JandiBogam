package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dto.MealCommentDto;

public interface MealCommentService {
    int writeComment(MealCommentDto commentDto);
}
