package com.backend.recruitAi.result.service;

import com.backend.recruitAi.result.dto.InterviewRequestDto;
import com.backend.recruitAi.result.dto.InterviewResponseDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import com.backend.recruitAi.result.repository.InterviewRepository;
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
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;
    private final InterviewResultRepository interviewResultRepository;

    // 새로운 인터뷰 결과 저장 (컨트롤러에서 DTO를 받아 처리)
    @Transactional
    public InterviewResponseDto saveInterviewResult(Long memberId, InterviewRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 찾을 수 없습니다."));

        Interview interview = Interview.builder()
                .member(member)
                .uuid(requestDto.getUuid())
                .job(requestDto.getJob())
                .career(requestDto.getCareer())
                .type(requestDto.getType())
                .level(requestDto.getLevel())
                .language(requestDto.getLanguage())
                .build();

        Interview savedResult = interviewRepository.save(interview);

        List<InterviewResult> answerAnalyses = requestDto.getAnswerAnalyses().stream()
                .map(dto -> InterviewResult.builder()
                        .interview(savedResult)
                        .seq(dto.getSeq())
                        .question(dto.getQuestion())
                        .answer(dto.getAnswer())
                        .good(dto.getGood())
                        .bad(dto.getBad())
                        .emotion_score(dto.getEmotionScore())
                        .emotion_text(dto.getEmotionText())
                        .tracking_score(dto.getTrackingScore())
                        .tracking_text(dto.getTrackingText())
                        .build())
                .collect(Collectors.toList());

        answerAnalyses.forEach(interviewResultRepository::save);

        // 연관 관계 설정
        savedResult.setAnswerAnalyses(answerAnalyses);

        return InterviewResponseDto.fromEntity(savedResult);
    }

    // 기존 인터뷰 결과 저장 (엔티티 직접 전달)
    @Transactional
    public Interview saveInterviewResult(Interview interview) {
        return interviewRepository.save(interview);
    }

    // 모든 인터뷰 결과 조회
    public List<InterviewResponseDto> getAllInterviewResults(Long memberId) {
        List<Interview> results = interviewRepository.findAllByMemberId(memberId);
        return results.stream()
                .map(InterviewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 ID의 인터뷰 결과 조회
    public InterviewResponseDto getInterviewResultById(Long id, Long memberId) {
        Interview result = interviewRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 권한이 없습니다."));
        return InterviewResponseDto.fromEntity(result);
    }

    // 인터뷰 결과 삭제
    @Transactional
    public void deleteInterviewResult(Long id, Long memberId) {
        Optional<Interview> resultOptional = interviewRepository.findByIdAndMemberId(id, memberId);
        if (resultOptional.isPresent()) {
            interviewRepository.delete(resultOptional.get());
        } else {
            throw new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 삭제 권한이 없습니다.");
        }
    }
}