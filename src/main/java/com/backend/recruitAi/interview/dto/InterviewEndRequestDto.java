package com.backend.recruitAi.interview.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewEndRequestDto {
    private String interviewId;
    private int lastSeq;  // /end에서만 사용
}
