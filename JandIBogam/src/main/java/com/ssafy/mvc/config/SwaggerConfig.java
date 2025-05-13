package com.ssafy.mvc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JandI Bogam REST API")
                        .description("Spring Boot을 이용한 JandI Bogam 건강관리 서비스 REST API 명세서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("JandI Bogam Team")
                                .email("jandibogam@ssafy.com")
                                .url("https://www.jandibogam.com"))
                        .license(new License()
                                .name("Apache License Version 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0"))
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                )
                .addTagsItem(new Tag().name("File API").description("파일 업로드 및 관리 관련 API"));
    }
}