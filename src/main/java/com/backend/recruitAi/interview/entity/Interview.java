package com.backend.recruitAi.interview.entity;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.result.entity.InterviewResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid; // UUID

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "job", nullable = false)
    private String job;

    @Column(name = "career", nullable = false)
    private String career; // ex) 신입, 경력 1년차 등

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InterviewType type; // PERSONALITY / TECHNICAL / MIXED

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level; // 상 / 중 / 하

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language; // KOREAN / ENGLISH

    @Column(name = "count", nullable = false)
    private Integer count; // 질문 개수

    @Builder.Default
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InterviewResult> answerAnalyses = new ArrayList<>();
}