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
    private String question;

    @Lob // 긴 텍스트를 저장하기 위해 CLOB 타입으로 매핑
    @Column(name = "interview_answer", nullable = false, columnDefinition = "LONGTEXT") // 답변내용
    private String answer;

    @Lob
    @Column(name = "interview_answer_good", nullable = false, columnDefinition = "LONGTEXT") // 잘한점
    private String good;

    @Lob
    @Column(name = "interview_answer_bad", nullable = false, columnDefinition = "LONGTEXT") // 못한점
    private String bad;

    @Column(name = "score", nullable = false)
    private int score;

    @Lob
    @Column(name = "emotion_text", nullable = true, columnDefinition = "TEXT")
    private String emotion_text;

    @Lob
    @Column(name = "mediapipe_text", nullable = true, columnDefinition = "TEXT")
    private String mediapipe_text;

    @Column(name = "emotion_score", nullable = false)
    private int emotion_score;

    @Column(name = "blink_score", nullable = false)
    private int blink_score;

    @Column(name = "eye_score", nullable = false)
    private int eye_score;

    @Column(name = "head_score", nullable = false)
    private int head_score;

    @Column(name = "hand_score", nullable = false)
    private int hand_score;
}