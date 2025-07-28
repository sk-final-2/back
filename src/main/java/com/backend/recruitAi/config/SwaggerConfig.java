package com.backend.recruitAi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // API 문서 정보 설정
        Info info = new Info()
                .title("RecruitAi Document")
                .description("Sample API documentation for RecruitAi application.")
                .version("v1.0.0");

        // JWT 인증 방식 설정 (Authorization 헤더에 Bearer 토큰 추가)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization"); // 스키마 이름과 일치

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("Authorization", securityScheme)) // "bearerAuth"는 스키마의 논리적 이름
                .addSecurityItem(securityRequirement); // 이 스키마를 모든 API에 기본적으로 적용
    }
}
