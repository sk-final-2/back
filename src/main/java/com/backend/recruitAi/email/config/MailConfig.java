package com.backend.recruitAi.email.config;

import com.backend.recruitAi.config.NaverMailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final NaverMailProperties naverMailProperties;

    @Bean(name = "naverMailSender")
    public JavaMailSender naverMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(naverMailProperties.getHost());
        mailSender.setPort(naverMailProperties.getPort());
        mailSender.setUsername(naverMailProperties.getUsername());
        mailSender.setPassword(naverMailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        naverMailProperties.getProperties().forEach(props::put);

        return mailSender;
    }
}