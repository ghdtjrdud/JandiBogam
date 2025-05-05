package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.UserDto;

import java.util.List;

public interface UserDao {


    int insert(UserDto userDto);

    List<UserDto> select();

    int update(UserDto userDto);

    int delete(int id);

    UserDto detail(int id);
}
