package com.backend.recruitAi.interview.dto;

import com.backend.recruitAi.interview.entity.AnswerAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerAnalysisResponseDto {
    private Long id;
    private Long interviewResultId;
    private Integer questionNumber;
    private String question;
    private String sttAnswerText;
    private String analysisFeedback;
    private Integer answerScore;

    public static AnswerAnalysisResponseDto fromEntity(AnswerAnalysis entity) {
        return AnswerAnalysisResponseDto.builder()
                .id(entity.getId())
                .interviewResultId(entity.getInterviewResult().getId())
                .questionNumber(entity.getQuestionNumber())
                .question(entity.getQuestion())
                .sttAnswerText(entity.getSttAnswerText())
                .analysisFeedback(entity.getAnalysisFeedback())
                .answerScore(entity.getAnswerScore())
                .build();
    }
}
