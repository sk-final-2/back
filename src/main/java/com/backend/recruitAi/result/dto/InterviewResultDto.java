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
    private Double score;

    private String emotionText;

    private String mediapipeText;

    @NotNull(message = "감정 점수는 필수입니다.")
    private Double emotionScore;

    private Double blinkScore;

    private Double eyeScore;

    private Double headScore;

    private Double handScore;

    public static InterviewResultDto fromEntity(InterviewResult entity) {
        return InterviewResultDto.builder()
                .seq(entity.getSeq())
                .question(entity.getQuestion())
                .answer(entity.getAnswer())
                .good(entity.getGood())
                .bad(entity.getBad())
                .score(entity.getScore())
                .emotionText(entity.getEmotion_text())
                .mediapipeText(entity.getMediapipe_text())
                .emotionScore(entity.getEmotion_score())
                .blinkScore(entity.getBlink_score())
                .eyeScore(entity.getEye_score())
                .headScore(entity.getHead_score())
                .handScore(entity.getHand_score())
                .build();
    }
}
