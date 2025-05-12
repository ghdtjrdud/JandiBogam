package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.MealCommentDao;
import com.ssafy.mvc.model.dto.MealCommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealCommentServiceImpl implements MealCommentService{

    private final MealCommentDao mealCommentDao;

    @Autowired
    public MealCommentServiceImpl(MealCommentDao mealCommentDao) {
        this.mealCommentDao = mealCommentDao;
    }

    @Override
    public int writeComment(MealCommentDto commentDto) {
        return mealCommentDao.insertComment(commentDto);
    }
}
