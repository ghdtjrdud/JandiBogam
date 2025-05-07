package com.ssafy;

import com.ssafy.mvc.model.repository.RefreshTokenRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JandIBogamApplication {

    public static void main(String[] args) {
        SpringApplication.run(JandIBogamApplication.class, args);
    }

}
