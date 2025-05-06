package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.AuthDto;
import com.ssafy.mvc.model.service.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private final AuthServiceImpl authService;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthDto.SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(
            summary = "로그인",
            description = "아이디/패스워드로 인증하고 JWT 토큰을 발급받습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = AuthDto.TokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        AuthDto.TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

}
