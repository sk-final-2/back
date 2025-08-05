package com.backend.recruitAi.result.dto;

import com.backend.recruitAi.interview.entity.InterviewType;
import com.backend.recruitAi.interview.entity.Level;
import com.backend.recruitAi.interview.entity.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequestDto {
    @NotBlank(message = "UUID는 필수입니다.")
    private String uuid;

    @NotBlank(message = "직무는 필수입니다.")
    private String job;

    @NotBlank(message = "경력 정보는 필수입니다.")
    private String career;

    @NotNull(message = "면접 유형은 필수입니다.")
    private InterviewType type;

    @NotNull(message = "난이도는 필수입니다.")
    private Level level;

    @NotNull(message = "언어는 필수입니다.")
    private Language language;

    @NotEmpty(message = "답변 분석 결과는 하나 이상 포함되어야 합니다.")
    private List<InterviewResultDto> answerAnalyses;
}