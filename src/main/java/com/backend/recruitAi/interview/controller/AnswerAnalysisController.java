package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.global.exception.ErrorCode; // ErrorCode 임포트
import com.backend.recruitAi.interview.dto.AnswerAnalysisRequestDto;
import com.backend.recruitAi.interview.dto.AnswerAnalysisResponseDto;
import com.backend.recruitAi.interview.entity.AnswerAnalysis;
import com.backend.recruitAi.interview.entity.InterviewResult;
import com.backend.recruitAi.interview.service.AnswerAnalysisService;
import com.backend.recruitAi.interview.service.InterviewResultService;
import com.backend.recruitAi.member.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer-analyses")
public class AnswerAnalysisController {

    private final AnswerAnalysisService answerAnalysisService;
    private final InterviewResultService interviewResultService;

    // 새로운 답변 분석 결과 저장 (특정 InterviewResult에 종속)
    @PostMapping("/interview-result/{interviewResultId}")
    public ResponseDto<AnswerAnalysisResponseDto> createAnswerAnalysis(
            @PathVariable Long interviewResultId,
            @Valid @RequestBody AnswerAnalysisRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            InterviewResult interviewResult = interviewResultService.getInterviewResultById(interviewResultId)
                    .filter(ir -> ir.getMember().getId().equals(userDetails.getMember().getId()))
                    .orElseThrow(() -> new NoSuchElementException("해당 인터뷰 결과를 찾을 수 없거나 접근 권한이 없습니다."));

            AnswerAnalysis answerAnalysis = requestDto.toEntity(interviewResult);
            AnswerAnalysis savedAnalysis = answerAnalysisService.saveAnswerAnalysis(answerAnalysis);
            return ResponseDto.success(AnswerAnalysisResponseDto.fromEntity(savedAnalysis));
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND); // 상세 메시지 제외
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR); // 상세 메시지 제외
        }
    }

    // 특정 InterviewResult에 속한 모든 답변 분석 결과 조회 (본인 것만)
    @GetMapping("/interview-result/{interviewResultId}")
    public ResponseDto<List<AnswerAnalysisResponseDto>> getAnswerAnalysesByInterviewResult(
            @PathVariable Long interviewResultId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            InterviewResult interviewResult = interviewResultService.getInterviewResultById(interviewResultId)
                    .filter(ir -> ir.getMember().getId().equals(userDetails.getMember().getId()))
                    .orElseThrow(() -> new NoSuchElementException("해당 인터뷰 결과를 찾을 수 없거나 접근 권한이 없습니다."));

            List<AnswerAnalysis> analyses = answerAnalysisService.getAnswerAnalysesByInterviewResult(interviewResult);
            List<AnswerAnalysisResponseDto> responseDtos = analyses.stream()
                    .map(AnswerAnalysisResponseDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseDto.success(responseDtos);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 ID의 답변 분석 결과 조회 (본인 것만)
    @GetMapping("/{id}")
    public ResponseDto<AnswerAnalysisResponseDto> getAnswerAnalysisById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AnswerAnalysis analysis = answerAnalysisService.getAnswerAnalysisById(id)
                    .orElseThrow(() -> new NoSuchElementException("답변 분석을 찾을 수 없습니다."));

            if (!analysis.getInterviewResult().getMember().getId().equals(userDetails.getMember().getId())) {
                return ResponseDto.error(ErrorCode.FORBIDDEN); // 상세 메시지 제외
            }

            return ResponseDto.success(AnswerAnalysisResponseDto.fromEntity(analysis));
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 답변 분석 결과 삭제 (본인 것만)
    @DeleteMapping("/{id}")
    public ResponseDto<Void> deleteAnswerAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AnswerAnalysis analysis = answerAnalysisService.getAnswerAnalysisById(id)
                    .orElseThrow(() -> new NoSuchElementException("답변 분석을 찾을 수 없습니다."));

            if (!analysis.getInterviewResult().getMember().getId().equals(userDetails.getMember().getId())) {
                return ResponseDto.error(ErrorCode.FORBIDDEN); // 상세 메시지 제외
            }

            answerAnalysisService.deleteAnswerAnalysis(id);
            return ResponseDto.success(null);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
