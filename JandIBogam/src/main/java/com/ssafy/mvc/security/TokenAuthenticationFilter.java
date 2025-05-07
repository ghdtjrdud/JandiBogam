package com.ssafy.mvc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //요청에서 토큰 추출
            String token = resolveToken(request);
            logger.info("토큰 추출: " + (token != null ? "성공" : "실패"));

            //유효한 토큰인지 확인
            if(StringUtils.hasText(token)) {
                boolean isValid = jwtTokenProvider.validateToken(token);
                logger.info("토큰 검증 결과: " + (isValid ? "유효함" : "유효하지 않음"));

                if(isValid) {
                    //토큰에서 사용자 ID 추출
                    String loginId = jwtTokenProvider.getLoginId(token);
                    logger.info("로그인 ID: " + loginId);

                    //인증 정보 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginId, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetails(request));

                    //인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("토큰 인증 중 오류 발생: " + e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    //요청 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
