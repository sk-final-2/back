package com.backend.recruitAi.result.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.result.service.InterviewResultService;
import com.backend.recruitAi.member.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer-analyses")
public class InterviewResultController {

    private final InterviewResultService interviewResultService;

    @DeleteMapping("/{id}")
    public ResponseDto<Void> deleteAnswerAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            interviewResultService.deleteAnswerAnalysis(id, userDetails.getMember().getId());
            return ResponseDto.success(null);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.ANSWER_ANALYSIS_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ResponseDto.error(ErrorCode.NO_AUTHORITY);
        }
    }
}