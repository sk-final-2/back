package com.backend.recruitAi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mail.gmail")
@Getter @Setter
public class GmailMailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private Map<String, Object> properties = new HashMap<>();
    private String defaultEncoding = "UTF-8";
}