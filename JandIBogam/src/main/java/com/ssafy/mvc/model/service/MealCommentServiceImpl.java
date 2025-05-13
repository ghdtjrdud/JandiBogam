package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.MealCommentDao;
import com.ssafy.mvc.model.dto.MealCommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<MealCommentDto> getCommentsByMealId(int mealId) {
        return mealCommentDao.selectCommentById(mealId);
    }

    @Override
    public int updateComment(MealCommentDto mealCommentDto) {
        return mealCommentDao.updateComment(mealCommentDto);
    }

    @Override
    public int deleteComment(int id) {
        return mealCommentDao.deleteComment(id);
    }

}
