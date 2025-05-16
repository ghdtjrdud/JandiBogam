package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.GroupDto;
import com.ssafy.mvc.model.dto.UserDto;
import com.ssafy.mvc.model.service.GroupService;
import com.ssafy.mvc.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final JwtTokenProvider jwtTokenProvider;
//    private final JwtUtil jwtUtil;

    @Autowired
    public GroupController(GroupService groupService, JwtTokenProvider jwtTokenProvider) {
        this.groupService = groupService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //    그룹생성
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

    //    그룹 가입
    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestBody GroupDto groupDto, HttpServletRequest request) {

        try {
//            bearer 빼고 토큰헤더 갖고오기
            String token = request.getHeader("Authorization").substring(7);
            Claims claims = jwtTokenProvider.getClaims(token);
            int userId = claims.get("id", Integer.class);

            int result = groupService.joinGroup(groupDto.getCode(), userId);

            if (result > 0) {
                return ResponseEntity.status(HttpStatus.OK).body("그룹 가입 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("그룹 가입 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패");
        }
    }

    //    내가 속한 그룹 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyGroup(HttpServletRequest request) {

        try {
//            String token = request.getHeader("Authorization").substring(7);
//            Claims claims = jwtTokenProvider.getClaims(token);
//            int userId = claims.get("id", Integer.class);
            System.out.println("hhi!");
            int userId = jwtTokenProvider.getUserIdFromRequest(request);

            System.out.println("userId : " + userId);

            GroupDto groupDto = groupService.getGroupByUserId(userId);

            if (groupDto != null) {
                return ResponseEntity.status(HttpStatus.OK).body(groupDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입된 그룹이 없습니다.");
            }

        } catch (Exception e) {
            System.out.println("에러메세지 :" + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패");
        }

    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable int groupId, HttpServletRequest request) {

        try {
            int userId = jwtTokenProvider.getUserIdFromRequest(request);

            int result = groupService.leaveGroup(groupId, userId);

            if (result > 0) {
                return ResponseEntity.status(HttpStatus.OK).body("그룹 탈퇴 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("그룹 탈퇴 실패");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패");
        }
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable int groupId, HttpServletRequest request) {

        try {

//            여기에서는 딱히 없어도 되는데 혹시 나중에 권한설정이나 이런거 있을수도
//            어차피 우리는 목록에서부터 가입된거만 보이게 할것
            int userId = jwtTokenProvider.getUserIdFromRequest(request);

            List<UserDto> list = groupService.getGroupMembers(groupId);

            if (!list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(list);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("맴버 조회 실패");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패");
        }

    }

}




