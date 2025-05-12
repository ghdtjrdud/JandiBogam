package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dto.AuthDto;

public interface AuthService {
    /**
     * 회원가입 처리
     * @param request 회원가입 요청 DTO
     */
    void signup(AuthDto.SignupRequest request);

    /**
     * 로그인 처리
     * @param request 로그인 요청 DTO
     * @return 토큰 응답 DTO
     */
    AuthDto.TokenResponse login(AuthDto.LoginRequest request);

    /**
     * 로그아웃 처리
     * @param token 접근 토큰
     */
    void logout(String token);
}
