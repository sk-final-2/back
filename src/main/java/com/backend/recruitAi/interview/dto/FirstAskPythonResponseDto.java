package com.backend.recruitAi.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirstAskPythonResponseDto {
    private String interviewId;        // UUID

    @JsonProperty("interview_question")
    private String interviewQuestion;  // 첫 번째 질문
}
