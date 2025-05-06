package com.ssafy.mvc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
@Getter
@Setter
public class JwtConfig {
    private String secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
}