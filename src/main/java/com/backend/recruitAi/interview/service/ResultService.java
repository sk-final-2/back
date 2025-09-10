package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.interview.dto.InterviewTempResponseDto;
import com.backend.recruitAi.interview.dto.InterviewTempResultDto;
import com.backend.recruitAi.interview.dto.TimeStampDto;
import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.repository.InterviewRepository;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import com.backend.recruitAi.result.service.AvgScoreService;
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
    private final AvgScoreService avgScoreService;

    @Transactional
    public void saveInterviewResult(String interviewId, Long memberId) {
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
                    .score(parseDoubleOrDefault(data.get("sttScore"), 0))
                    .emotion_score(parseDoubleOrDefault(data.get("emotionScore"), 0))
                    .emotion_text((String) data.getOrDefault("emotionText", null))
                    .mediapipe_text((String) data.getOrDefault("trackingText", null))
                    .blink_score(parseDoubleOrDefault(data.get("blinkScore"), 0))
                    .eye_score(parseDoubleOrDefault(data.get("eyeScore"), 0))
                    .head_score(parseDoubleOrDefault(data.get("headScore"), 0))
                    .hand_score(parseDoubleOrDefault(data.get("handScore"), 0))
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

        // 캐시 무효화
        avgScoreService.evictByInterview(interview);

        // Redis 데이터 정리
        for (int i = 1; i <= lastSeq; i++) {
            redisTemplate.delete(baseKey + ":seq:" + i);
        }
        redisTemplate.delete(baseKey + ":lastSeq");

    }

    @Transactional
    public InterviewTempResponseDto viewInterviewTemp(String interviewId) {
        String baseKey = "interview:" + interviewId;
        Object lastSeqObj = redisTemplate.opsForValue().get(baseKey + ":lastSeq");
        if (lastSeqObj == null) throw new IllegalArgumentException("면접이 완료되지 않았습니다.");

        int lastSeq = Integer.parseInt(lastSeqObj.toString());

        // 인터뷰 메타 정보
        Interview interview = interviewRepository.findByUuid(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 면접 정보가 없습니다."));

        List<InterviewTempResultDto> tempResults = new ArrayList<>();

        for (int i = 1; i <= lastSeq; i++) {
            String key = baseKey + ":seq:" + i;
            Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

            // 1) 타임스탬프: emotion + tracking 합치기
            List<TimeStampDto> timestamps = new ArrayList<>();
            timestamps.addAll(readTimestampList(data.get("emotionTimestamps")));
            timestamps.addAll(readTimestampList(data.get("trackingTimestamps")));

            // (선택) time 기준 정렬 (mm:ss or hh:mm:ss)
            timestamps.sort(Comparator.comparing(ts -> safeTimeToSeconds(ts.getTime())));

            // 2) temp DTO (프론트 응답용)
            InterviewTempResultDto dto = InterviewTempResultDto.builder()
                    .seq(i)
                    .question((String) data.getOrDefault("question", ""))
                    .answer((String) data.getOrDefault("answer", ""))
                    .good((String) data.getOrDefault("good", ""))
                    .bad((String) data.getOrDefault("bad", ""))
                    .score(parseDoubleOrDefault(data.get("sttScore"), 0))
                    .emotionText((String) data.getOrDefault("emotionText", null))
                    .mediapipeText((String) data.getOrDefault("trackingText", null))
                    .emotionScore(parseDoubleOrDefault(data.get("emotionScore"), 0))
                    .blinkScore(parseDoubleOrDefault(data.get("blinkScore"), 0))
                    .eyeScore(parseDoubleOrDefault(data.get("eyeScore"), 0))
                    .headScore(parseDoubleOrDefault(data.get("headScore"), 0))
                    .handScore(parseDoubleOrDefault(data.get("handScore"), 0))
                    .timestamp(timestamps)
                    .build();


            tempResults.add(dto);

        }

        // 전체 평균 점수 1건 계산
        //AvgScoreDto avgScore = interviewResultRepository.findAllAverageScores().orElseThrow(() -> new BusinessException(ErrorCode.AVAERAGE_ERROR));
        // 직무별 평균 점수 계산
        AvgScoreDto avgScore = Optional.ofNullable(
                avgScoreService.getAvgByJob(interview.getJob())).orElseThrow(() -> new BusinessException(ErrorCode.AVERAGE_ERROR));

        // 최종 응답 래핑
        return InterviewTempResponseDto.of(
                interview,
                tempResults,
                Collections.singletonList(avgScore)
        );
    }

    // ---- 유틸 ----

    @SuppressWarnings("unchecked")
    private List<TimeStampDto> readTimestampList(Object raw) {
        if (raw == null) return Collections.emptyList();

        // 저장 방식 그대로: List<Map<String, Object>> 또는 단일 Map 방어
        if (raw instanceof List<?> list) {
            List<TimeStampDto> out = new ArrayList<>();
            for (Object e : list) {
                if (e instanceof Map<?, ?> m) {
                    TimeStampDto ts = new TimeStampDto();
                    ts.setTime(Objects.toString(m.get("time"), null));
                    ts.setReason(Objects.toString(m.get("reason"), null));
                    out.add(ts);
                }
            }
            return out;
        }
        if (raw instanceof Map<?, ?> m) {
            TimeStampDto ts = new TimeStampDto();
            ts.setTime(Objects.toString(m.get("time"), null));
            ts.setReason(Objects.toString(m.get("reason"), null));
            return List.of(ts);
        }
        return Collections.emptyList();
    }

    private int safeTimeToSeconds(String mmss) {
        if (mmss == null || mmss.isEmpty()) return Integer.MAX_VALUE;
        try {
            String[] sp = mmss.split(":");
            if (sp.length == 2) {
                int m = Integer.parseInt(sp[0]);
                int s = Integer.parseInt(sp[1]);
                return m * 60 + s;
            } else if (sp.length == 3) {
                int h = Integer.parseInt(sp[0]);
                int m = Integer.parseInt(sp[1]);
                int s = Integer.parseInt(sp[2]);
                return h * 3600 + m * 60 + s;
            }
        } catch (Exception ignored) {}
        return Integer.MAX_VALUE;
    }

    private int parseIntOrDefault(Object value, int defaultVal) {
        try { return Integer.parseInt(value.toString()); }
        catch (Exception e) { return defaultVal; }
    }
    private double parseDoubleOrDefault(Object value, double defaultVal) {
        if (value == null) return defaultVal;
        try {
            if (value instanceof Number) {
                double v = ((Number) value).doubleValue();
                return Double.isFinite(v) ? v : defaultVal;
            }
            String s = value.toString().trim().replace(",", "");
            double v = Double.parseDouble(s);
            return Double.isFinite(v) ? v : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

}
