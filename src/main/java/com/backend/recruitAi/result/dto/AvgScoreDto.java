package com.backend.recruitAi.result.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvgScoreDto {
    private int score;
    private int emotionScore;
    private int blinkScore;
    private int eyeScore;
    private int headScore;
    private int handScore;
}
