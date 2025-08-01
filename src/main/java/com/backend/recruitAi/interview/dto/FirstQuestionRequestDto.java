package com.backend.recruitAi.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirstQuestionRequestDto {
    private String job;       // 직무
    private int count;        // 질문 개수
    private String ocrText;   // OCR 결과 텍스트
    private int seq;          // 질문 순서
}