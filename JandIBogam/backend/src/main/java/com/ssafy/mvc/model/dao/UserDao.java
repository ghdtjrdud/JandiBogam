package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao {


    int insert(UserDto userDto);

    List<UserDto> select();

    UserDto selectById(int id);

    int update(UserDto userDto);

    int delete(int id);

    UserDto detail(int id);

    UserDto findByLoginId(@NotBlank @Size(min = 6, max = 12) String loginId);

    List<Integer> selectUserDiseases(int userId);

    boolean updateTheme(@Param("id") int id, @Param("newTheme") String newTheme);
}
