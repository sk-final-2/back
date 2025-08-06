package com.backend.recruitAi.interview.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
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

        redisTemplate.expire(key, Duration.ofHours(1));

        tryPublishIfComplete(interviewId);
    }


    public void savePartialEmotion(String interviewId, int seq, Map<String, Object> emotion) {
        String key = "interview:" + interviewId + ":seq:" + seq;
        redisTemplate.opsForHash().put(key, "emotionScore", emotion.get("score"));
        redisTemplate.opsForHash().put(key,"emotionText",emotion.get("text"));
        redisTemplate.expire(key, Duration.ofHours(1));
        tryPublishIfComplete(interviewId);
    }

    public void tryPublishIfComplete(String interviewId) {
        String baseKey = "interview:" + interviewId;
        Object lastSeqObj = redisTemplate.opsForValue().get(baseKey + ":lastSeq");

        // 아직 end 요청이 오지 않았으면 중단
        if (lastSeqObj == null) {
            log.info("아직 lastSeq 없음");
            return;
        }

        int lastSeq = Integer.parseInt(lastSeqObj.toString());

        for (int i = 1; i <= lastSeq; i++) {
            String key = baseKey + ":seq:" + i;
            Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

            boolean emotionDone = data.containsKey("emotionScore");
            boolean sttDone = data.containsKey("sttScore");
            //boolean gazeDone = data.containsKey("gazeScore");
            boolean gazeDone = true;
            if (!(emotionDone && sttDone && gazeDone)) {
                return;
            }
        }

        // 모든 seq 완료됨
        redisTemplate.convertAndSend("interview:" + interviewId + ":done", "done");
    }


}
