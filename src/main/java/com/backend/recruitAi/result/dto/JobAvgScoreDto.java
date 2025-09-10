package com.backend.recruitAi.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobAvgScoreDto {
    // 정상화된 직무 키 (UPPER(TRIM(job)))
    private String job;
    private Double score;
    private Double emotionScore;
    private Double blinkScore;
    private Double eyeScore;
    private Double headScore;
    private Double handScore;
}
