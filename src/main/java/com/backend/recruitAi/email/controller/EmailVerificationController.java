package com.backend.recruitAi.email.controller;

import com.backend.recruitAi.email.service.EmailVerificationService;
import com.backend.recruitAi.email.dto.EmailRequestDto;
import com.backend.recruitAi.email.dto.EmailVerificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestBody EmailRequestDto request) {
        System.out.println("📩 이메일 인증 요청 도착: " + request.getEmail());
        verificationService.sendCode(request.getEmail());
        return ResponseEntity.ok("인증 메일이 전송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody EmailVerificationRequestDto request) {
        boolean success = verificationService.verifyCode(request.getEmail(), request.getCode());
        return success
                ? ResponseEntity.ok("인증 성공")
                : ResponseEntity.status(400).body("인증 실패");
    }
}

