package com.backend.recruitAi.interview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisInterviewService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void savePartialSTT(String interviewId, int seq, Map<String, Object> stt, String question) {
        String key = "interview:" + interviewId + ":seq:" + seq;

        Map<String, Object> value = Map.of(
                "question", question,
                "answer", stt.get("interview_answer"),
                "good", stt.get("interview_answer_good"),
                "bad", stt.get("interview_answer_bad"),
                "sttScore", stt.get("score")
        );

        redisTemplate.opsForHash().putAll(key, value);
    }


    public void savePartialEmotion(String interviewId, int seq, Map<String, Object> emotion) {
        String key = "interview:" + interviewId + ":seq:" + seq;
        redisTemplate.opsForHash().put(key, "emotionScore", emotion.get("score"));
    }
}
