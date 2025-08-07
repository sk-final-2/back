package com.backend.recruitAi.result.entity;

import com.backend.recruitAi.interview.entity.Interview;
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
@Table(name = "interview_result")
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @Column(name = "seq", nullable = false)
    private int seq;

    @Column(name = "question", nullable = false, length = 255)
    private String question; // 질문내용

    @Lob // 긴 텍스트를 저장하기 위해 CLOB 타입으로 매핑
    @Column(name = "interview_answer", nullable = false) // 답변내용
    private String answer;

    @Lob
    @Column(name = "interview_answer_good", nullable = false) // 잘한점
    private String good;

    @Lob
    @Column(name = "interview_answer_bad", nullable = false) // 못한점
    private String bad;

    @Column(name = "score", nullable = false) // 총 점수
    private int score;

    @Column(name = "emotion_score", nullable = false) // 표정점수
    private int emotion_score;

    @Lob
    @Column(name = "emotion_text", nullable = true, columnDefinition = "TEXT") // 감정표현텍스트
    private String emotion_text;

    @Column(name = "tracking_score", nullable = true) // 시선처리점수
    private int tracking_score;

    @Lob
    @Column(name = "tracking_text", nullable = true, columnDefinition = "TEXT") // 시선처리표현텍스트
    private String tracking_text;
}