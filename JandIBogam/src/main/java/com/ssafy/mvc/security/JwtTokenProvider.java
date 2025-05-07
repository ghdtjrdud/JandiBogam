package com.ssafy.mvc.security;

import com.ssafy.mvc.config.JwtConfig;
import com.ssafy.mvc.model.dto.UserDto;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider { //**jwt 토큰 생성, 검증, 정보 추출 기능**

    private final JwtConfig jwtConfig;

    // 토큰 생성 메서드 - 유효기간을 매개변수로 받음
    public String generateToken(UserDto user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰 생성 로직
    private String makeToken(Date expiry, UserDto user) {
        Date now = new Date();
        String secretKey = jwtConfig.getSecretKey();

        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT Secret key is not configured properly");
        }

        System.out.println("JWT 시크릿 키: " + secretKey.substring(0, 5) + "..."); // 보안을 위해 일부만 출력
        System.out.println("만료 시간: " + expiry);

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
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8)) // UTF-8 인코딩 명시
                .compact();
    }

    // 토큰 유효성 검증 메서드
    public boolean validateToken(String token) {
        try {
            // null 체크 추가
            if (token == null || token.isEmpty()) {
                System.out.println("토큰 검증 실패: 토큰이 비어있습니다.");
                return false;
            }

            String secretKey = jwtConfig.getSecretKey();
            if (secretKey == null || secretKey.isEmpty()) {
                System.out.println("토큰 검증 실패: 시크릿 키가 구성되지 않았습니다.");
                return false;
            }

            // 비밀값으로 복호화 (UTF-8 인코딩 명시)
            Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 검증 실패: 만료된 토큰입니다. " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("토큰 검증 실패: 잘못된 형식의 토큰입니다. " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println("토큰 검증 실패: 서명 검증에 실패했습니다. " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }

    // 토큰에서 로그인 아이디 추출
    public String getLoginId(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        String secretKey = jwtConfig.getSecretKey();
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT Secret key is not configured properly");
        }

        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
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