package com.backend.recruitAi.result.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.result.dto.InterviewRequestDto;
import com.backend.recruitAi.result.dto.InterviewResponseDto;
import com.backend.recruitAi.result.service.InterviewService;
import com.backend.recruitAi.member.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview-results")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseDto<InterviewResponseDto> createInterviewResult(
            @Valid @RequestBody InterviewRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            InterviewResponseDto responseDto = interviewService.saveInterviewResult(userDetails.getMember().getId(), requestDto);
            return ResponseDto.success(responseDto);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseDto<List<InterviewResponseDto>> getAllInterviewResults(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<InterviewResponseDto> responseDtos = interviewService.getAllInterviewResults(userDetails.getMember().getId());
        return ResponseDto.success(responseDtos);
    }

    @GetMapping("/{interviewId}")
    public ResponseDto<InterviewResponseDto> getInterviewResultById(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            InterviewResponseDto responseDto = interviewService.getInterviewResultById(interviewId, userDetails.getMember().getId());
            return ResponseDto.success(responseDto);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.INTERVIEW_RESULT_NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto<Void> deleteInterviewResult(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            interviewService.deleteInterviewResult(id, userDetails.getMember().getId());
            return ResponseDto.success(null);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.INTERVIEW_RESULT_NOT_FOUND);
        }
    }
}