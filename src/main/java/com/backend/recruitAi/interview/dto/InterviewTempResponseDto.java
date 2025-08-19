package com.backend.recruitAi.interview.dto;

import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewTempResponseDto {
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

    // 미리보기 결과(타임스탬프 포함)
    private List<InterviewTempResultDto> answerAnalyses;

    // 평균 점수(전체 평균 1건만 담아도 되면 List<AvgScoreDto>에 1개 넣어서 전달)
    private List<AvgScoreDto> avgScore;

    public static InterviewTempResponseDto of(
            Interview entity,
            List<InterviewTempResultDto> tempResults,
            List<AvgScoreDto> avgScoreList
    ) {
        return InterviewTempResponseDto.builder()
                .uuid(entity.getUuid())
                .memberId(entity.getMember().getId())
                .createdAt(entity.getCreatedAt())
                .job(entity.getJob())
                .career(entity.getCareer())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .level(entity.getLevel() != null ? entity.getLevel().name() : null)
                .language(entity.getLanguage() != null ? entity.getLanguage().name() : null)
                .count(entity.getCount())
                .answerAnalyses(tempResults)
                .avgScore(avgScoreList)
                .build();
    }
}
