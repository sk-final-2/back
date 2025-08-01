package com.backend.recruitAi.result.entity;

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

    @Column(name = "seq", nullable = false)
    private int seq;

    @Column(name = "question", nullable = false, length = 255)
    private String question; // 질문내용

    @Lob // 긴 텍스트를 저장하기 위해 CLOB 타입으로 매핑
    @Column(name = "answer", nullable = false) // 답변내용
    private String answer;

    @Lob
    @Column(name = "good", nullable = false) // 잘한점
    private String good;

    @Lob
    @Column(name = "bad", nullable = false) // 못한점
    private String bad;

    @Column(name = "emotion", nullable = false) // 표정점수
    private int emotion;

    @Column(name = "tracking", nullable = true) // 시선처리
    private int tracking;
}