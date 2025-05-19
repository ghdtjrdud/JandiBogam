package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.UserDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ssafy.mvc.model.service.UserService;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<?> insert(@RequestBody UserDto user) {

        int result = userService.insert(user);

        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패");
        }

    }

    @GetMapping
    public ResponseEntity<?> select() {

        List<UserDto> list = userService.select();

        if (list != null) {
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("조회 실패");
        }

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable("userId") int id, @RequestBody UserDto userDto) {

        userDto.setId(id);

        int result = userService.update(userDto);

        if (result == 1) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("수정 실패");
        }

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable("userId") int id) {

        int result = userService.delete(id);

        if (result == 1) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
        }

    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> detail(@PathVariable("userId") int id) {

        UserDto userDto = userService.detail(id);

        if (userDto != null) {

            return ResponseEntity.status(HttpStatus.OK).body(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청");
        }

    }

    @PatchMapping("/{id}/theme")
    public ResponseEntity<?> updateTheme(@PathVariable int id, @RequestBody Map<String, String> request) {

        String newTheme = request.get("themePreference");

        if (newTheme == null || !(newTheme.equals("default") || newTheme.equals("senior"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 테마입니다.");
        }

        boolean success = userService.updateTheme(id, newTheme);

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body("테마 변경 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("테마 변경 실패");
        }

    }
}
