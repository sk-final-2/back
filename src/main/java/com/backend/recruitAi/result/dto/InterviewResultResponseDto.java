package com.backend.recruitAi.result.dto;

import com.backend.recruitAi.result.entity.InterviewResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultResponseDto {

    private Long id;
    private Long memberId;
    private LocalDateTime createdAt;
    private List<AnswerAnalysisDto> answerAnalyses;

    public static InterviewResultResponseDto fromEntity(InterviewResult entity) {
        List<AnswerAnalysisDto> analysisDtos = entity.getAnswerAnalyses().stream()
                .map(AnswerAnalysisDto::fromEntity)
                .collect(Collectors.toList());

        return InterviewResultResponseDto.builder()
                .id(entity.getId())
                .memberId(entity.getMember().getId())
                .createdAt(entity.getCreatedAt())
                .answerAnalyses(analysisDtos)
                .build();
    }
}