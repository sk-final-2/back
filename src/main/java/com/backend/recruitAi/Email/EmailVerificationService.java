package com.backend.recruitAi.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository repository;
    private final MailService mailService;

    public void sendCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        EmailVerification ev = new EmailVerification(email, code, expiresAt, false);
        repository.save(ev);

        try {
            mailService.sendVerificationMail(email, code);
            System.out.println("✅ 이메일 전송 성공: " + email + " [" + code + "]");
        } catch (Exception e) {
            System.out.println("❌ 이메일 전송 실패: " + email);
            e.printStackTrace();  // 콘솔에 상세 SMTP 에러 로그 출력됨
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다.");
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        EmailVerification ev = repository.findByEmailAndCode(email, inputCode)
                .orElseThrow(() -> new IllegalArgumentException("인증 정보가 존재하지 않습니다."));

        if (ev.isVerified()) throw new IllegalStateException("이미 인증된 이메일입니다.");
        if (ev.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalStateException("코드가 만료되었습니다.");

        ev.setVerified(true);
        repository.save(ev);
        return true;
    }
}

