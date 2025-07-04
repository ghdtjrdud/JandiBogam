package com.ssafy.mvc.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class AuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequest { //**회원가입 요청**
        @NotBlank
        @Size(min = 6, max = 12)
        private String loginId;

        @NotBlank
        @Size(min = 8, max = 72, message = "비밀번호는 72자를 초과할 수 없습니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다")
        private String password;

        @NotBlank
        private String name;

        @NotBlank
        private String gender;

        private String email;

        private LocalDate birthDate;

        private String familyCode; // 가족 코드 (선택사항)

        private double height;

        private double weight;

        // 건강 정보
        private boolean diabetes; // 당뇨
        private boolean hypertension; // 고혈압
        private boolean heartDisease; // 심장질환
        private boolean kidneyDisease; // 신장질환
        private boolean liverDisease; // 간질환

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest { //**로그인 요청**
        @NotBlank
        private String loginId;

        @NotBlank
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse { //**토큰 응답(access, refresh토큰 포함)**
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private String loginId;
    }
}