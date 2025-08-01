package com.backend.recruitAi.interview.dto;

import com.backend.recruitAi.interview.entity.AnswerAnalysis;
import com.backend.recruitAi.interview.entity.InterviewResult;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerAnalysisRequestDto {
    @NotNull(message = "질문 번호는 필수입니다.")
    @Min(value = 1, message = "질문 번호는 1 이상이어야 합니다.")
    private Integer questionNumber;

    @NotBlank(message = "질문 내용은 필수입니다.")
    private String question;

    @NotBlank(message = "STT 답변 텍스트는 필수입니다.")
    private String sttAnswerText;

    @NotBlank(message = "분석 피드백은 필수입니다.")
    private String analysisFeedback;

    @NotNull(message = "답변 점수는 필수입니다.")
    private Integer answerScore;

    public AnswerAnalysis toEntity(InterviewResult interviewResult) {
        return AnswerAnalysis.builder()
                .interviewResult(interviewResult)
                .questionNumber(this.questionNumber)
                .question(this.question)
                .sttAnswerText(this.sttAnswerText)
                .analysisFeedback(this.analysisFeedback)
                .answerScore(this.answerScore)
                .build();
    }
}