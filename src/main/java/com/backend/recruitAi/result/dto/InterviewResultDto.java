package com.backend.recruitAi.result.dto;

import com.backend.recruitAi.result.entity.InterviewResult;
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
public class InterviewResultDto {
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

    @NotNull(message = "총 점수는 필수입니다.")
    private int score;

    @NotNull(message = "감정 점수는 필수입니다.")
    private int emotionScore;

    private String emotionText;

    private Integer trackingScore;

    private String trackingText;

    public static InterviewResultDto fromEntity(InterviewResult entity) {
        return InterviewResultDto.builder()
                .id(entity.getId())
                .seq(entity.getSeq())
                .question(entity.getQuestion())
                .answer(entity.getAnswer())
                .good(entity.getGood())
                .bad(entity.getBad())
                .score(entity.getScore())
                .emotionScore(entity.getEmotion_score())
                .emotionText(entity.getEmotion_text())
                .trackingScore(entity.getTracking_score())
                .trackingText(entity.getTracking_text())
                .build();
    }
}
