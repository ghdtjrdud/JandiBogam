package com.ssafy.mvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads/meals}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        Path uploadDirPath = Paths.get(uploadDir);
//        String uploadAbsolutePath = uploadDirPath.toFile().getAbsolutePath();

        // 로그 추가
//        System.out.println("실제 업로드 디렉토리 경로: " + uploadAbsolutePath);
//        System.out.println("리소스 핸들러 매핑: /uploads/** -> file:" + uploadAbsolutePath + "/");

        // 수정된 부분: 경로 끝에 / 추가, 업로드 디렉토리에서 meals 디렉토리로 수정
        // 업로드 폴더를 정적 리소스로 노출
        String path = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path.substring(0, path.lastIndexOf("/uploads") + 1));
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500") // Live Server의 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}