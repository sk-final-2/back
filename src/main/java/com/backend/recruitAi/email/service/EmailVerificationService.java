package com.backend.recruitAi.email.service;

import com.backend.recruitAi.email.repository.EmailVerificationRepository;
import com.backend.recruitAi.email.entity.EmailVerification;
import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository repository;
    private final MailService mailService;

    public void sendCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(3);

        EmailVerification ev = new EmailVerification(email, code, expiresAt, false);
        repository.save(ev);

        try {
            mailService.sendVerificationMail(email, code);
            System.out.println("✅ 이메일 전송 성공: " + email + " [" + code + "]");
        } catch (Exception e) {
            System.out.println("❌ 이메일 전송 실패: " + email);
            e.printStackTrace();  // 콘솔에 상세 SMTP 에러 로그 출력됨
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        EmailVerification ev = repository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!ev.getCode().equals(inputCode)) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        if (ev.isVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        if (ev.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.EMAIL_CODE_EXPIRED);
        }

        ev.setVerified(true);
        repository.save(ev);
        return true;
    }
}

