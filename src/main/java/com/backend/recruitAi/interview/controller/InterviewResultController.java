package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.global.exception.ErrorCode; // ErrorCode 임포트
import com.backend.recruitAi.interview.dto.InterviewResultRequestDto;
import com.backend.recruitAi.interview.dto.InterviewResultResponseDto;
import com.backend.recruitAi.interview.entity.InterviewResult;
import com.backend.recruitAi.interview.service.InterviewResultService;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
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
@RequestMapping("/api/interview-results")
public class InterviewResultController {

    private final InterviewResultService interviewResultService;
    private final MemberRepository memberRepository;

    // 새로운 인터뷰 결과 저장
    @PostMapping
    public ResponseDto<InterviewResultResponseDto> createInterviewResult(
            @Valid @RequestBody InterviewResultRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Member member = memberRepository.findById(userDetails.getMember().getId())
                    .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 찾을 수 없습니다."));

            InterviewResult interviewResult = requestDto.toEntity(member);
            InterviewResult savedResult = interviewResultService.saveInterviewResult(interviewResult);
            return ResponseDto.success(InterviewResultResponseDto.fromEntity(savedResult));
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 현재 로그인한 사용자의 모든 인터뷰 결과 조회
    @GetMapping
    public ResponseDto<List<InterviewResultResponseDto>> getMyInterviewResults(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Member member = memberRepository.findById(userDetails.getMember().getId())
                    .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 찾을 수 없습니다."));

            List<InterviewResult> results = interviewResultService.getInterviewResultsByMember(member);
            List<InterviewResultResponseDto> responseDtos = results.stream()
                    .map(InterviewResultResponseDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseDto.success(responseDtos);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 ID의 인터뷰 결과 조회 (본인 것만)
    @GetMapping("/{id}")
    public ResponseDto<InterviewResultResponseDto> getInterviewResultById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Member member = memberRepository.findById(userDetails.getMember().getId())
                    .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 찾을 수 없습니다."));

            InterviewResult result = interviewResultService.getInterviewResultById(id)
                    .filter(ir -> ir.getMember().getId().equals(member.getId())) // 본인 데이터 확인
                    .orElseThrow(() -> new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 접근 권한이 없습니다."));

            return ResponseDto.success(InterviewResultResponseDto.fromEntity(result));
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 인터뷰 결과 삭제 (본인 것만)
    @DeleteMapping("/{id}")
    public ResponseDto<Void> deleteInterviewResult(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Member member = memberRepository.findById(userDetails.getMember().getId())
                    .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 찾을 수 없습니다."));

            interviewResultService.getInterviewResultById(id)
                    .filter(ir -> ir.getMember().getId().equals(member.getId()))
                    .orElseThrow(() -> new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 삭제 권한이 없습니다."));

            interviewResultService.deleteInterviewResult(id);
            return ResponseDto.success(null);
        } catch (NoSuchElementException e) {
            return ResponseDto.error(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}