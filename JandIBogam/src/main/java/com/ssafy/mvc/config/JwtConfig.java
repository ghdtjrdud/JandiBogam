package com.ssafy.mvc.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig { //**jwt 설정 정보(시크릿 키, 토큰 만료 시간)
    private String secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    // 시크릿 키가 올바르게 로드되었는지 확인하는 초기화 메서드
    @PostConstruct
    public void init() {
        System.out.println("JWT Config initialized. Secret key present: " + (secretKey != null && !secretKey.isEmpty()));
        System.out.println("Access token expiration: " + accessTokenExpiration);
        System.out.println("Refresh token expiration: " + refreshTokenExpiration);
    }
}