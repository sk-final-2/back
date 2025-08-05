package com.backend.recruitAi.interview.dto;

import com.backend.recruitAi.interview.entity.InterviewType;
import com.backend.recruitAi.interview.entity.Level;
import com.backend.recruitAi.interview.entity.Language;
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
    private String career;    // 경력 (예: "신입", "경력 1년차")
    private InterviewType interviewType; // PERSONALITY, TECHNICAL, MIXED
    private Level level;                  // 상, 중, 하
    private Language language; // KOREAN, ENGLISH
}