package com.backend.recruitAi.email.config;

import com.backend.recruitAi.config.GmailMailProperties;
import com.backend.recruitAi.config.NaverMailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final NaverMailProperties naverProps;
    private final GmailMailProperties gmailProps;

    @Bean("naverMailSender")
    public JavaMailSender naverMailSender() {
        JavaMailSenderImpl ms = new JavaMailSenderImpl();
        ms.setHost(naverProps.getHost());
        ms.setPort(naverProps.getPort());
        ms.setUsername(naverProps.getUsername());
        ms.setPassword(naverProps.getPassword());
        ms.setDefaultEncoding(naverProps.getDefaultEncoding());
        Properties p = ms.getJavaMailProperties();
        naverProps.getProperties().forEach(p::put);

        p.setProperty("mail.smtp.ssl.enable", "true");
        p.remove("mail.smtp.starttls.enable");

        return ms;
    }

    @Bean("gmailMailSender")
    @Primary
    public JavaMailSender gmailMailSender() {
        JavaMailSenderImpl ms = new JavaMailSenderImpl();
        ms.setHost(gmailProps.getHost());
        ms.setPort(gmailProps.getPort());
        ms.setUsername(gmailProps.getUsername());
        ms.setPassword(gmailProps.getPassword());
        ms.setDefaultEncoding(gmailProps.getDefaultEncoding());
        Properties p = ms.getJavaMailProperties();
        gmailProps.getProperties().forEach(p::put);

        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.setProperty("mail.smtp.starttls.required", "true");
        p.remove("mail.smtp.ssl.enable");

        return ms;
    }
}