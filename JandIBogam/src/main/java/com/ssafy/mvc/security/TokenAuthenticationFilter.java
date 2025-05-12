package com.ssafy.mvc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청에서 토큰 추출
            String token = resolveToken(request);
            logger.info("토큰 추출: " + (token != null ? "성공" : "실패"));

            // 유효한 토큰인지 확인
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                logger.info("토큰 검증 결과: 유효함");

                // 사용자 정보를 이용한 인증 객체 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 인증 정보가 있으면 SecurityContext에 저장
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("인증 정보 설정 완료: " + authentication.getName());
                }
            } else if (StringUtils.hasText(token)) {
                logger.info("토큰 검증 결과: 유효하지 않음");
            }
        } catch (Exception e) {
            logger.error("토큰 인증 중 오류 발생: " + e.getMessage(), e);
            // 인증 실패 시 보안 컨텍스트 초기화
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}