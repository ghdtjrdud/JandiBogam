package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.GroupDto;
import com.ssafy.mvc.model.service.GroupService;
import com.ssafy.mvc.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public GroupController(GroupService groupService, JwtTokenProvider jwtTokenProvider) {
        this.groupService = groupService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/")
    public ResponseEntity<?> createGroup(@RequestBody GroupDto groupDto, HttpServletRequest request) {
        try {
            //헤더에서 토큰 꺼내기
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
            }
            String token = bearerToken.substring(7); // "Bearer " 제거

            //토큰에서 사용자 ID 꺼내기
            Claims claims = jwtTokenProvider.getClaims(token);
            int userId = claims.get("id", Integer.class); // 이게 createdBy가 됨

            // 3. 그룹 정보 구성
            groupDto.setCode(UUID.randomUUID().toString());
            groupDto.setCreatedBy(userId);

            int result = groupService.createGroup(groupDto);

            if (result > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body(groupDto);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("그룹 생성 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패: " + e.getMessage());
        }
    }


}
