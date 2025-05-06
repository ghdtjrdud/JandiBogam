package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public int insert(UserDto userDto) {
        return userDao.insert(userDto);
    }

    @Override
    public List<UserDto> select() {
        return userDao.select();
    }

    @Override
    public int update(UserDto userDto) {
        return userDao.update(userDto);
    }

    @Override
    public int delete(int id) {
        return userDao.delete(id);
    }

    @Override
    public UserDto detail(int id) {
        return userDao.detail(id);
    }


}
