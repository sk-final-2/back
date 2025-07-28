package com.backend.recruitAi.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationMail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[RecruitAi] 이메일 인증 코드");
        message.setText("인증 코드는: " + code + "입니다." + "\n유효시간 5분 안에 인증해주세요😎");
        message.setFrom("your_email@gmail.com");
        javaMailSender.send(message);
    }
}
