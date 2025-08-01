package com.backend.recruitAi.interview.dto;

import com.backend.recruitAi.interview.entity.InterviewResult;
import com.backend.recruitAi.member.entity.Member;
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
public class InterviewResultRequestDto {
    @NotBlank(message = "인터뷰 결과 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "전체 STT 텍스트는 필수입니다.")
    private String fullSttText;

    @NotBlank(message = "전체 분석 결과는 필수입니다.")
    private String overallAnalysis;

    @NotNull(message = "점수는 필수입니다.")
    private Integer score;

    public InterviewResult toEntity(Member member) {
        return InterviewResult.builder()
                .member(member)
                .title(this.title)
                .fullSttText(this.fullSttText)
                .overallAnalysis(this.overallAnalysis)
                .score(this.score)
                .build();
    }
}