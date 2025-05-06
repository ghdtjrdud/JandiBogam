package com.ssafy.mvc.config;

import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dto.UserDto;
import com.ssafy.mvc.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JwtConfig jwtConfig;

    @DisplayName("generateToken(): 유저정보와 만료기간을 전달해 토큰 만들기")
    @Test
    void generateToken() {
        // 테스트용 UserDto 객체 생성
        UserDto testUser = createTestUser();

        // 토큰 생성 (충분히 긴 만료 시간 설정)
        String token = jwtTokenProvider.generateToken(testUser, Duration.ofHours(1));

        // 토큰이 null이 아니고 빈 문자열이 아닌지 확인
        assertThat(token).isNotNull().isNotEmpty();

        // 생성된 토큰 유효성 검증
        boolean isValid = jwtTokenProvider.validateToken(token);
        assertThat(isValid).isTrue();

        // 토큰에서 로그인 아이디 추출 및 검증
        String loginId = jwtTokenProvider.getLoginId(token);
        assertThat(loginId).isEqualTo(testUser.getLoginId());
    }

    @DisplayName("generateAccessToken(): 액세스 토큰 생성 테스트")
    @Test
    void generateAccessToken() {
        // 테스트 전에 액세스 토큰 만료 시간을 임시로 늘림
        long originalExpiration = jwtConfig.getAccessTokenExpiration();
        jwtConfig.setAccessTokenExpiration(3600000); // 1시간으로 설정

        try {
            // 테스트용 UserDto 객체 생성
            UserDto testUser = createTestUser();

            // 액세스 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(testUser);

            // 토큰이 null이 아니고 빈 문자열이 아닌지 확인
            assertThat(accessToken).isNotNull().isNotEmpty();

            // 생성된 토큰 유효성 검증
            boolean isValid = jwtTokenProvider.validateToken(accessToken);
            assertThat(isValid).isTrue();
        } finally {
            // 원래 값으로 복원
            jwtConfig.setAccessTokenExpiration(originalExpiration);
        }
    }

    @DisplayName("generateRefreshToken(): 리프레시 토큰 생성 테스트")
    @Test
    void generateRefreshToken() {
        // 테스트 전에 리프레시 토큰 만료 시간을 임시로 늘림
        long originalExpiration = jwtConfig.getRefreshTokenExpiration();
        jwtConfig.setRefreshTokenExpiration(3600000); // 1시간으로 설정

        try {
            // 테스트용 UserDto 객체 생성
            UserDto testUser = createTestUser();

            // 리프레시 토큰 생성
            String refreshToken = jwtTokenProvider.generateRefreshToken(testUser);

            // 토큰이 null이 아니고 빈 문자열이 아닌지 확인
            assertThat(refreshToken).isNotNull().isNotEmpty();

            // 생성된 토큰 유효성 검증
            boolean isValid = jwtTokenProvider.validateToken(refreshToken);
            assertThat(isValid).isTrue();
        } finally {
            // 원래 값으로 복원
            jwtConfig.setRefreshTokenExpiration(originalExpiration);
        }
    }

    @DisplayName("validateToken(): 유효한 토큰과 만료된 토큰 검증")
    @Test
    void validateToken() {
        // 테스트용 UserDto 객체 생성
        UserDto testUser = createTestUser();

        // 유효한 토큰 생성 (1시간 후 만료)
        String validToken = jwtTokenProvider.generateToken(testUser, Duration.ofHours(1));

        // 만료된 토큰 생성 (1밀리초 후 만료)
        String expiredToken = jwtTokenProvider.generateToken(testUser, Duration.ofMillis(1));

        // 유효한 토큰 검증
        boolean isValidToken = jwtTokenProvider.validateToken(validToken);
        assertThat(isValidToken).isTrue();

        // 약간의 딜레이를 주어 토큰이 만료되도록 함
        try {
            Thread.sleep(10); // 10밀리초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 만료된 토큰 검증
        boolean isExpiredToken = jwtTokenProvider.validateToken(expiredToken);
        assertThat(isExpiredToken).isFalse();
    }


    // 테스트용 UserDto 객체 생성 메서드
    private UserDto createTestUser() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setLoginId("testuser");
        user.setPasswordHash("password");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender("M");
        user.setHeight(180.0);
        user.setWeight(75.0);
        user.setUiTheme("light");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}