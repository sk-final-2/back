package com.backend.recruitAi.email.controller;

import com.backend.recruitAi.common.dto.ResponseDto;
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
    public ResponseDto<String> sendCode(@RequestBody EmailRequestDto request) {
        System.out.println("📩 이메일 인증 요청 도착: " + request.getEmail());
        verificationService.sendCode(request.getEmail());
        return ResponseDto.success("이메일 인증 요청 완료");
    }

    @PostMapping("/verify")
    public ResponseDto<String> verify(@RequestBody EmailVerificationRequestDto request) {
        boolean success = verificationService.verifyCode(request.getEmail(), request.getCode());
        return success
                ? ResponseDto.success("인증 성공")
                : ResponseDto.error(400,"인증 실패","인증실패");
    }
}

