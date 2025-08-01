package com.backend.recruitAi.result.service;

import com.backend.recruitAi.result.dto.AnswerAnalysisDto;
import com.backend.recruitAi.result.dto.InterviewResultRequestDto;
import com.backend.recruitAi.result.dto.InterviewResultResponseDto;
import com.backend.recruitAi.result.entity.AnswerAnalysis;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.result.repository.AnswerAnalysisRepository;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewResultService {

    private final InterviewResultRepository interviewResultRepository;
    private final MemberRepository memberRepository;
    private final AnswerAnalysisRepository answerAnalysisRepository;

    // 새로운 인터뷰 결과 저장 (컨트롤러에서 DTO를 받아 처리)
    @Transactional
    public InterviewResultResponseDto saveInterviewResult(Long memberId, InterviewResultRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 찾을 수 없습니다."));

        InterviewResult interviewResult = InterviewResult.builder()
                .member(member)
                .build();
        InterviewResult savedResult = interviewResultRepository.save(interviewResult);

        List<AnswerAnalysis> answerAnalyses = requestDto.getAnswerAnalyses().stream()
                .map(dto -> AnswerAnalysis.builder()
                        .interviewResult(savedResult)
                        .seq(dto.getSeq())
                        .question(dto.getQuestion())
                        .answer(dto.getAnswer())
                        .good(dto.getGood())
                        .bad(dto.getBad())
                        .emotion(dto.getEmotion())
                        .tracking(dto.getTracking())
                        .build())
                .collect(Collectors.toList());

        answerAnalyses.forEach(answerAnalysisRepository::save);

        // 연관 관계 설정
        savedResult.setAnswerAnalyses(answerAnalyses);

        return InterviewResultResponseDto.fromEntity(savedResult);
    }

    // 기존 인터뷰 결과 저장 (엔티티 직접 전달)
    @Transactional
    public InterviewResult saveInterviewResult(InterviewResult interviewResult) {
        return interviewResultRepository.save(interviewResult);
    }

    // 모든 인터뷰 결과 조회
    public List<InterviewResultResponseDto> getAllInterviewResults(Long memberId) {
        List<InterviewResult> results = interviewResultRepository.findAllByMemberId(memberId);
        return results.stream()
                .map(InterviewResultResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 ID의 인터뷰 결과 조회
    public InterviewResultResponseDto getInterviewResultById(Long id, Long memberId) {
        InterviewResult result = interviewResultRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 권한이 없습니다."));
        return InterviewResultResponseDto.fromEntity(result);
    }

    // 인터뷰 결과 삭제
    @Transactional
    public void deleteInterviewResult(Long id, Long memberId) {
        Optional<InterviewResult> resultOptional = interviewResultRepository.findByIdAndMemberId(id, memberId);
        if (resultOptional.isPresent()) {
            interviewResultRepository.delete(resultOptional.get());
        } else {
            throw new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 삭제 권한이 없습니다.");
        }
    }
}