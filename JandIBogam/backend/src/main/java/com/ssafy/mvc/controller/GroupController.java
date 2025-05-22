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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public GroupController(GroupService groupService, JwtTokenProvider jwtTokenProvider) {
        this.groupService = groupService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //    그룹생성
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupDto groupDto) {
        try {
            //헤더에서 토큰 꺼내기
//            String bearerToken = request.getHeader("Authorization");
//            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
//            }
//            String token = bearerToken.substring(7); // "Bearer " 제거
//
//            //토큰에서 사용자 ID 꺼내기
//            Claims claims = jwtTokenProvider.getClaims(token);
//            int userId = claims.get("id", Integer.class); // 이게 createdBy가 됨

            int userId = jwtTokenProvider.extractUserId(request);

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
    public ResponseEntity<?> joinGroup(@RequestBody GroupDto groupDto) {

        try {

            int userId = jwtTokenProvider.extractUserId(request);

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
    public ResponseEntity<?> getMyGroup() {

        try {
            int userId = jwtTokenProvider.extractUserId(request);

            List<GroupDto> groupList = groupService.getGroupByUserId(userId);

            if (groupList != null) {
                return ResponseEntity.status(HttpStatus.OK).body(groupList);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입된 그룹이 없습니다.");
            }

        } catch (Exception e) {
            System.out.println("에러메세지 :" + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패");
        }

    }

    //    그룹탈퇴
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable int groupId, HttpServletRequest request) {

        try {
            int userId = jwtTokenProvider.extractUserId(request);

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

    //    맴버조회(가입한 그룹만 어차피 목록에서 보이기때문에 따로 권한 설정  x)
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable int groupId, HttpServletRequest request) {

        try {

//            여기에서는 딱히 없어도 되는데 혹시 나중에 권한설정이나 이런거 있을수도
//            어차피 우리는 목록에서부터 가입된거만 보이게 할것
            jwtTokenProvider.extractUserId(request);

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

    @GetMapping("/{groupId}/code")
    public ResponseEntity<Map<String, String>> getGroupCode(
            @PathVariable int groupId) {

        // 1) JWT 토큰 검증: 로그인 여부만 확인
        jwtTokenProvider.extractUserId(request);

        // 2) 서비스에서 그룹 코드 조회
        String code = groupService.getGroupCode(groupId);

        // 3) 200 OK + { "code": "ABC123" } 반환
        return ResponseEntity.ok(Collections.singletonMap("code", code));
    }

//    그룹 상세조회
    @GetMapping("/{groupId}")
    public ResponseEntity<?> detaulGroup(@PathVariable int groupId) {

        try {
            jwtTokenProvider.extractUserId(request);

            GroupDto groupDto = groupService.detailGroup(groupId);

            if (groupDto != null) {
                return ResponseEntity.status(HttpStatus.OK).body(groupDto);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다. 상세조회 실패");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 인증 실패");
        }

    }

}




