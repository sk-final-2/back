package com.backend.recruitAi.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Qualifier("gmailMailSender")
    private final JavaMailSender gmailMailSender;

    @Qualifier("naverMailSender")
    private final JavaMailSender naverMailSender;

    public void sendVerificationMail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[RecruitAi] 이메일 인증 코드");
        message.setText("인증 코드는: " + code + "입니다." + "\n유효시간 3분 안에 인증해주세요😎");

        //도메인 확인 후 전송
        if (to.toLowerCase().endsWith("@naver.com")) {
            message.setFrom("chlwldmschlwl2002@naver.com");
            System.out.println("📧 네이버 SMTP 사용: " + to);
            naverMailSender.send(message);
        } else {
            message.setFrom("ssjjjieun0429@gmail.com");
            System.out.println("📧 Gmail SMTP 사용: " + to);
            gmailMailSender.send(message);
        }
    }
}
