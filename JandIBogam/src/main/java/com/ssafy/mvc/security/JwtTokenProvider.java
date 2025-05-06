package com.ssafy.mvc.security;

import com.ssafy.mvc.config.JwtConfig;
import com.ssafy.mvc.model.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    // 토큰 생성 메서드 - 유효기간을 매개변수로 받음
    public String generateToken(UserDto user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰 생성 로직
    private String makeToken(Date expiry, UserDto user) {
        Date now = new Date();
        return Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 타입 설정
                .setIssuer("jandiBogam") // 발급자 설정
                .setIssuedAt(now) // 발급시간
                .setExpiration(expiry) // 만료시간
                .setSubject(user.getLoginId()) // 토큰 제목 -> 사용자 아이디
                // Claims에 사용자 정보 추가
                .claim("id", user.getId())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("role", "ROLE_USER") // 역할 정보 - 필요에 따라 수정 가능
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey().getBytes()) // 바이트로 변환하여 서명
                .compact();
    }

    // 토큰 유효성 검증 메서드
    public boolean validateToken(String token) {
        try {
            // 비밀값으로 복호화 (바이트로 변환)
            Jwts.parser().setSigningKey(jwtConfig.getSecretKey().getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("토큰 검증 실패: " + e.getMessage()); // 오류 메시지 출력 추가
            return false; // 복호화 과정에서 에러가 나면 유효하지 않은 토큰으로
        }
    }

    // 토큰에서 로그인 아이디 추출
    public String getLoginId(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecretKey().getBytes()).parseClaimsJws(token).getBody();
    }

    // 액세스 토큰 생성 - 유효기간은 설정에서 가져옴
    public String generateAccessToken(UserDto user) {
        return generateToken(user, Duration.ofMillis(jwtConfig.getAccessTokenExpiration()));
    }

    // 리프레시 토큰 생성 - 유효기간은 설정에서 가져옴
    public String generateRefreshToken(UserDto user) {
        return generateToken(user, Duration.ofMillis(jwtConfig.getRefreshTokenExpiration()));
    }
}