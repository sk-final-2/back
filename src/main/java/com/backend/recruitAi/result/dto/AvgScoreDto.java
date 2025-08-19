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
    private double score;
    private double emotionScore;
    private double blinkScore;
    private double eyeScore;
    private double headScore;
    private double handScore;
}
