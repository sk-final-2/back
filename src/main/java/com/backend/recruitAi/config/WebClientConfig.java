package com.backend.recruitAi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("http://192.168.0.239:8000") // 주소는 실제로 사용할 파이썬 서버의 포트로 변경해야 합니다.
                .build();
    }
}
