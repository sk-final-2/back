package com.backend.recruitAi.result.dto;

import com.backend.recruitAi.result.entity.AnswerAnalysis;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerAnalysisDto {
    private Long id;

    @NotNull(message = "순번은 필수입니다.")
    private int seq;

    @NotBlank(message = "질문 내용은 필수입니다.")
    private String question;

    @NotBlank(message = "답변 내용은 필수입니다.")
    private String answer;

    @NotBlank(message = "잘한 점은 필수입니다.")
    private String good;

    @NotBlank(message = "못한 점은 필수입니다.")
    private String bad;

    @NotNull(message = "감정 점수는 필수입니다.")
    private int emotion;

    private int tracking;

    public static AnswerAnalysisDto fromEntity(AnswerAnalysis entity) {
        return AnswerAnalysisDto.builder()
                .id(entity.getId())
                .seq(entity.getSeq())
                .question(entity.getQuestion())
                .answer(entity.getAnswer())
                .good(entity.getGood())
                .bad(entity.getBad())
                .emotion(entity.getEmotion())
                .tracking(entity.getTracking())
                .build();
    }
}
