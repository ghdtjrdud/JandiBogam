package com.ssafy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        exclude = {
                SecurityAutoConfiguration.class
        }
)
//@MapperScan("com.ssafy.jandibogam.dao")
public class JandIBogamApplication {

    public static void main(String[] args) {
        SpringApplication.run(JandIBogamApplication.class, args);
    }

}
