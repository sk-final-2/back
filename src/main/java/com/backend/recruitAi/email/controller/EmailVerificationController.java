package com.backend.recruitAi.email.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.email.service.EmailVerificationService;
import com.backend.recruitAi.email.dto.EmailRequestDto;
import com.backend.recruitAi.email.dto.EmailVerificationRequestDto;
import lombok.RequiredArgsConstructor;
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
        if (!success) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        return ResponseDto.success("인증 성공");
    }
}

