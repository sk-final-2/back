package com.backend.recruitAi.result.dto;

import com.backend.recruitAi.interview.entity.Interview;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class InterviewResponseDto {

    private Long id;
    private String uuid;
    private Long memberId;
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm:ss")
    private LocalDateTime createdAt;
    private String job;
    private String career;
    private String type;
    private String level;
    private String language;
    private Integer count;
    private List<InterviewResultDto> answerAnalyses;

    private List<AvgScoreDto> avgScore;

    public static InterviewResponseDto fromEntity(Interview entity, List<AvgScoreDto> avgScoreList) {
        List<InterviewResultDto> analysisDtos = entity.getAnswerAnalyses().stream()
                .map(InterviewResultDto::fromEntity)
                .collect(Collectors.toList());

        return InterviewResponseDto.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .memberId(entity.getMember().getId())
                .createdAt(entity.getCreatedAt())
                .job(entity.getJob())
                .career(entity.getCareer())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .level(entity.getLevel() != null ? entity.getLevel().name() : null)
                .language(entity.getLanguage() != null ? entity.getLanguage().name() : null)
                .count(entity.getCount())
                .answerAnalyses(analysisDtos)
                .avgScore(avgScoreList)
                .build();
    }
}