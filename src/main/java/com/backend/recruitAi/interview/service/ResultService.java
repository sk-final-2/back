package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.result.dto.InterviewResponseDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.repository.InterviewRepository;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final InterviewRepository interviewRepository;
    private final InterviewResultRepository interviewResultRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public InterviewResponseDto saveAndGetInterviewResult(String interviewId, Long memberId) {
        String baseKey = "interview:" + interviewId;
        Object lastSeqObj = redisTemplate.opsForValue().get(baseKey + ":lastSeq");

        if (lastSeqObj == null) throw new IllegalArgumentException("면접이 완료되지 않았습니다.");

        int lastSeq = Integer.parseInt(lastSeqObj.toString());
        List<InterviewResult> results = new ArrayList<>();

        for (int i = 1; i <= lastSeq; i++) {
            String key = baseKey + ":seq:" + i;
            Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

            InterviewResult result = InterviewResult.builder()
                    .seq(i)
                    .question((String) data.getOrDefault("question", ""))
                    .answer((String) data.getOrDefault("answer", ""))
                    .good((String) data.getOrDefault("good", ""))
                    .bad((String) data.getOrDefault("bad", ""))
                    .score(parseIntOrDefault(data.get("sttScore"), 0))
                    .emotion_score(parseIntOrDefault(data.get("emotionScore"), 0))
                    .emotion_text((String) data.getOrDefault("emotionText", null))
                    .tracking_score(parseIntOrDefault(data.get("trackingScore"), 0))
                    .tracking_text((String) data.getOrDefault("trackingText", null))
                    .build();

            results.add(result);
        }

        // 인터뷰 정보 조회 (uuid = interviewId)
        Interview interview = interviewRepository.findByUuid(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 면접 정보가 없습니다."));

        // 인터뷰 결과 연관관계 설정
        for (InterviewResult result : results) {
            result.setInterview(interview);
        }
        interview.getAnswerAnalyses().clear();
        interview.getAnswerAnalyses().addAll(results);

        // DB 저장
        interviewResultRepository.saveAll(results);

        // Redis 데이터 정리
        for (int i = 1; i <= lastSeq; i++) {
            redisTemplate.delete(baseKey + ":seq:" + i);
        }
        redisTemplate.delete(baseKey + ":lastSeq");

        // 응답 DTO 반환
        return InterviewResponseDto.fromEntity(interview);
    }

    private int parseIntOrDefault(Object value, int defaultVal) {
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
