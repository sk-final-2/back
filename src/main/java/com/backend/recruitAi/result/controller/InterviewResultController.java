package com.backend.recruitAi.result.controller;

import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.result.dto.InterviewResultRequestDto;
import com.backend.recruitAi.result.dto.InterviewResultResponseDto;
import com.backend.recruitAi.result.service.InterviewResultService;
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
public class InterviewResultController {

    private final InterviewResultService interviewResultService;

    @PostMapping
    public ResponseDto<InterviewResultResponseDto> createInterviewResult(
            @Valid @RequestBody InterviewResultRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            InterviewResultResponseDto responseDto = interviewResultService.saveInterviewResult(userDetails.getMember().getId(), requestDto);
            return ResponseDto.success(responseDto);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseDto<List<InterviewResultResponseDto>> getAllInterviewResults(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<InterviewResultResponseDto> responseDtos = interviewResultService.getAllInterviewResults(userDetails.getMember().getId());
        return ResponseDto.success(responseDtos);
    }

    @GetMapping("/{interviewId}")
    public ResponseDto<InterviewResultResponseDto> getInterviewResultById(
            @PathVariable Long interviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            InterviewResultResponseDto responseDto = interviewResultService.getInterviewResultById(interviewId, userDetails.getMember().getId());
            return ResponseDto.success(responseDto);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.INTERVIEW_RESULT_NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseDto<Void> deleteInterviewResult(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            interviewResultService.deleteInterviewResult(id, userDetails.getMember().getId());
            return ResponseDto.success(null);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.INTERVIEW_RESULT_NOT_FOUND);
        }
    }
}