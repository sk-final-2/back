package com.backend.recruitAi.interview.dto;

import com.backend.recruitAi.interview.entity.InterviewResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResultResponseDto {
    private Long id;
    private Long memberId;
    private String title;
    private String fullSttText;
    private String overallAnalysis;
    private int score;
    private LocalDateTime interviewDate;

    public static InterviewResultResponseDto fromEntity(InterviewResult entity) {
        return InterviewResultResponseDto.builder()
                .id(entity.getId())
                .memberId(entity.getMember().getId())
                .title(entity.getTitle())
                .fullSttText(entity.getFullSttText())
                .overallAnalysis(entity.getOverallAnalysis())
                .score(entity.getScore())
                .interviewDate(entity.getInterviewDate())
                .build();
    }
}