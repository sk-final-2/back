package com.backend.recruitAi.result.service;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.backend.recruitAi.result.dto.InterviewRequestDto;
import com.backend.recruitAi.result.dto.InterviewResponseDto;
import com.backend.recruitAi.result.dto.JobAvgScoreDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import com.backend.recruitAi.interview.repository.InterviewRepository;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;
    private final InterviewResultRepository interviewResultRepository;
    private final AvgScoreService avgScoreService;

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
                .count(requestDto.getAnswerAnalyses().size())
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
                        .score(dto.getScore())
                        .emotion_score(dto.getEmotionScore())
                        .emotion_text(dto.getEmotionText())
                        .mediapipe_text(dto.getMediapipeText())
                        .blink_score(dto.getBlinkScore())
                        .eye_score(dto.getEyeScore())
                        .head_score(dto.getHeadScore())
                        .hand_score(dto.getHandScore())
                        .build())
                .collect(Collectors.toList());

        answerAnalyses.forEach(interviewResultRepository::save);

        savedResult.setAnswerAnalyses(answerAnalyses);

        //AvgScoreDto avgScore = avgScoreService.calculateAverageScores(answerAnalyses);
        //AvgScoreDto avgScore = interviewResultRepository.findAllAverageScores().orElseThrow(() -> new BusinessException(ErrorCode.AVAERAGE_ERROR));
        // 직무별 평균 점수 계산
        AvgScoreDto avgScore = Optional.ofNullable(
                avgScoreService.getAvgByJob(interview.getJob())).orElseThrow(() -> new BusinessException(ErrorCode.AVERAGE_ERROR));

        // 쓰기 후 캐시 무효화(커밋 후부터 새 값으로 다시 채워짐)
        avgScoreService.evictByInterview(interview);

        return InterviewResponseDto.fromEntity(savedResult, Collections.singletonList(avgScore));
    }

    // 기존 인터뷰 결과 저장 (엔티티 직접 전달)
    @Transactional
    public Interview saveInterviewResult(Interview interview) {
        return interviewRepository.save(interview);
    }

    // 모든 인터뷰 결과 조회
    public List<InterviewResponseDto> getAllInterviewResults(Long memberId) {
        List<Interview> results = interviewRepository.findAllByMemberId(memberId);
        if (results.isEmpty()) return Collections.emptyList();

        // 1) 응답에 필요한 직무 키들을 표준화해서 수집 (UPPER(TRIM))
        Set<String> jobKeys = results.stream()
                .map(Interview::getJob)
                .filter(Objects::nonNull)
                .map(this::normJobKey) // UPPER + TRIM
                .collect(Collectors.toSet());

        // 2) 한 번의 쿼리로 모든 직무 평균 가져오기
        List<JobAvgScoreDto> grouped = interviewResultRepository.findAverageScoresByJobs(jobKeys);

        // 3) 직무키 -> AvgScoreDto 매핑
        Map<String, AvgScoreDto> avgMap = grouped.stream()
                .collect(Collectors.toMap(
                        JobAvgScoreDto::getJob, // 이미 UPPER(TRIM) 형태로 반환됨
                        j -> new AvgScoreDto(
                                j.getScore(), j.getEmotionScore(),
                                j.getBlinkScore(), j.getEyeScore(),
                                j.getHeadScore(), j.getHandScore()
                        )
                ));

        // 4) 인터뷰별로 평균 붙여 응답 생성
        return results.stream()
                .map(iv -> {
                    String key = normJobKey(iv.getJob());
                    AvgScoreDto avg = avgMap.get(key);
                    if (avg == null) throw new BusinessException(ErrorCode.AVERAGE_ERROR);
                    return InterviewResponseDto.fromEntity(iv, Collections.singletonList(avg));
                })
                .collect(Collectors.toList());
    }

    // 키 표준화: UPPER(TRIM(job))와 동일하게
    private String normJobKey(String job) {
        return job == null ? null : job.trim().toUpperCase();
    }


    // 특정 ID의 인터뷰 결과 조회
    public InterviewResponseDto getInterviewResultById(Long id, Long memberId) {
        Interview result = interviewRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new NoSuchElementException("인터뷰 결과를 찾을 수 없거나 권한이 없습니다."));

        //List<InterviewResult> interviewResults = interviewResultRepository.findAllByInterview(result);
        // ✅ AvgScoreService를 호출하여 평균 점수 계산
        //AvgScoreDto avgScore = interviewResultRepository.findAllAverageScores().orElseThrow(() -> new BusinessException(ErrorCode.AVAERAGE_ERROR));
        // 직무별 평균 점수 계산
        AvgScoreDto avgScore = Optional.ofNullable(
                avgScoreService.getAvgByJob(result.getJob())).orElseThrow(() -> new BusinessException(ErrorCode.AVERAGE_ERROR));

        return InterviewResponseDto.fromEntity(result, Collections.singletonList(avgScore));
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