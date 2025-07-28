package com.backend.recruitAi.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestBody Map<String, String> request) {
        System.out.println("📩 이메일 인증 요청 도착: " + request);
        String email = request.get("email");
        verificationService.sendCode(email);
        return ResponseEntity.ok("인증 메일이 전송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean success = verificationService.verifyCode(email, code);
        return success
                ? ResponseEntity.ok("인증 성공")
                : ResponseEntity.status(400).body("인증 실패");
    }
}

