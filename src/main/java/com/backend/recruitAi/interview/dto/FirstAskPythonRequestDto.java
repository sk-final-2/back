package com.backend.recruitAi.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirstAskPythonRequestDto {
    private String text;        // OCR 텍스트
    private String job;         // 직무
    private String interviewId; // UUID
    private int seq;            // 질문 순서
}
