package com.backend.recruitAi.interview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "answer_analyses")
public class AnswerAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_result_id", nullable = false)
    private InterviewResult interviewResult;

    @Column(nullable = false)
    private int questionNumber; // 질문 번호

    @Column(nullable = false, length = 500)
    private String question; // 질문 내용

    @Lob // Text 타입으로 매핑
    @Column(nullable = false)
    private String sttAnswerText; // 해당 질문에 대한 STT 답변 텍스트

    @Lob // Text 타입으로 매핑
    @Column(nullable = false)
    private String analysisFeedback; // 해당 답변에 대한 AI 분석 피드백

    @Column(nullable = false)
    private int answerScore; // 해당 답변에 대한 점수
}
