package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dto.UserDto;

import java.util.List;


public interface UserService {
    int insert(UserDto user);

    List<UserDto> select();

    int update(UserDto userDto);

    int delete(int id);

    UserDto detail(int id);

    boolean updateTheme(int id, String newTheme);

    UserDto findByLoginId(String loginId);
}
