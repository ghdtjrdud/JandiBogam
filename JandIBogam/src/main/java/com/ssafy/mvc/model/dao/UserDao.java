package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface UserDao {


    int insert(UserDto userDto);

    List<UserDto> select();

    int update(UserDto userDto);

    int delete(int id);

    UserDto detail(int id);

    UserDto findByLoginId(@NotBlank @Size(min = 6, max = 12) String loginId);
}
