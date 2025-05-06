package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dto.AuthDto;
import com.ssafy.mvc.model.dto.UserDto;
import com.ssafy.mvc.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입 처리
    public UserDto signup(AuthDto.SignupRequest request){
        //아이디 중복 확인
        if(userDao.findByLoginId(request.getLoginId()) != null){
            throw new RuntimeException("이미 사용중인 아이디입니다.");
        }

        //사용자 객체 생성
        UserDto user = new UserDto();
        user.setLoginId(request.getLoginId());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); //비밀번호 암호화
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setBirthDate(request.getBirthDate());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());

        // 건강 정보 올바르게 설정
        user.setDiabetes(request.isDiabetes());
        user.setAllergies(request.isAllergies());
        user.setHeartDisease(request.isHeartDisease());
        user.setHyperlipidemia(request.isHyperlipidemia());
        user.setHypertension(request.isHypertension());
        user.setKidneyDisease(request.isKidneyDisease());

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userDao.insert(user);
        return user;
    }

    //로그인 처리 및 jwt 토큰 발급
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request){
        //사용자 조회
        UserDto user = userDao.findByLoginId(request.getLoginId());
        if(user == null){
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        //비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new RuntimeException("일치하지 않는 비밀번호입니다.");
        }

        //JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return new AuthDto.TokenResponse(accessToken, refreshToken, "Bearer", user.getLoginId());
    }
}