package com.ssafy.mvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads/meals}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드 디렉토리의 절대 경로 생성
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();

        // 다양한 URL 패턴을 모두 지원하도록 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/")
                .setCachePeriod(3600); // 1시간 캐시

        // 상위 uploads 디렉토리도 매핑 (./uploads/ 전체)
        String parentPath = Paths.get("./uploads/").toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + parentPath + "/")
                .setCachePeriod(3600);

        // 디버깅용 로그
        System.out.println("=== 정적 리소스 매핑 설정 ===");
        System.out.println("meals 업로드 디렉토리: " + uploadDir);
        System.out.println("meals 절대 경로: " + absolutePath);
        System.out.println("전체 uploads 경로: " + parentPath);
        System.out.println("URL 매핑: /uploads/** -> 두 경로 모두 지원");

        // 실제 파일 존재 확인
        try {
            java.io.File dir = new java.io.File(absolutePath);
            System.out.println("디렉토리 존재 여부: " + dir.exists());
            if (dir.exists()) {
                java.io.File[] files = dir.listFiles();
                System.out.println("디렉토리 내 파일 수: " + (files != null ? files.length : 0));
            }
        } catch (Exception e) {
            System.out.println("디렉토리 확인 중 오류: " + e.getMessage());
        }
    }
}