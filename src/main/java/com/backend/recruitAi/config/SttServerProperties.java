package com.backend.recruitAi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "stt.server")
public class SttServerProperties {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }
}
