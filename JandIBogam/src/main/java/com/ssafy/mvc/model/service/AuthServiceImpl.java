package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dto.AuthDto;
import com.ssafy.mvc.model.dto.UserDto;
import com.ssafy.mvc.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void signup(AuthDto.SignupRequest request) {
        // 중복 ID 검사
        if (userDao.findByLoginId(request.getLoginId()) != null) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // UserDto 생성 및 저장
        UserDto user = new UserDto();
        user.setLoginId(request.getLoginId());
        user.setPasswordHash(hashedPassword);
        user.setName(request.getName());
        user.setBirthDate(request.getBirthDate() != null ? request.getBirthDate() : LocalDate.now());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setFamilyCode(request.getFamilyCode());

        // 건강 정보 설정
        user.setDiabetes(request.isDiabetes());
        user.setHypertension(request.isHypertension());
        user.setHeartDisease(request.isHeartDisease());
        user.setKidneyDisease(request.isKidneyDisease());
        user.setLiverDisease(request.isLiverDisease());

        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userDao.insert(user);
    }

    @Override
    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        // 사용자 조회
        UserDto user = userDao.findByLoginId(request.getLoginId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 응답 DTO 생성
        return new AuthDto.TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getLoginId()
        );
    }

    @Override
    @Transactional
    public void logout(String token) {
        // MyBatis 환경에서는 클라이언트 측에서 토큰 삭제를 처리
        // 서버 측에서는 특별한 로직이 필요 없음
    }
}