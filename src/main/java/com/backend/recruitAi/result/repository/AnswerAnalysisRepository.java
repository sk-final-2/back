package com.backend.recruitAi.result.repository;

import com.backend.recruitAi.result.entity.AnswerAnalysis;
import com.backend.recruitAi.result.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerAnalysisRepository extends JpaRepository<AnswerAnalysis, Long> {
    List<AnswerAnalysis> findByInterviewResult(InterviewResult interviewResult);
}