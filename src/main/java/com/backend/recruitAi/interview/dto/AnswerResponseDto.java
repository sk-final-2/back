package com.backend.recruitAi.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerResponseDto {
    private String interviewId;
    private String newQuestion;
    private boolean keepGoing;
}
