package com.backend.recruitAi.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirstQuestionResponseDto {
    private String interviewId; // 새로 생성한 인터뷰 ID
    private String question;  // 생성된 질문
    private int seq;          // 질문 순서
}
