package com.backend.recruitAi.result.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultRequestDto {
    @NotEmpty(message = "답변 분석 결과는 하나 이상 포함되어야 합니다.")
    private List<AnswerAnalysisDto> answerAnalyses;
}