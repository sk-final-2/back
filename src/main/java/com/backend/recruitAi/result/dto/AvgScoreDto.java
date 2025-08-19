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
    private Double score;
    private Double emotionScore;
    private Double blinkScore;
    private Double eyeScore;
    private Double headScore;
    private Double handScore;
}
