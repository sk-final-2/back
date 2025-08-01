package com.backend.recruitAi.interview.entity;

import com.backend.recruitAi.member.entity.Member; // Member 엔티티 임포트
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "interview_results")
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String title;

    @Lob // Text 타입으로 매핑 (긴 문자열 저장)
    @Column(nullable = false)
    private String fullSttText;

    @Lob
    @Column(nullable = false)
    private String overallAnalysis;

    @Column(nullable = false)
    private int score;

    @CreationTimestamp
    private LocalDateTime interviewDate;
}
