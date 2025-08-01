package com.backend.recruitAi.interview.repository;

import com.backend.recruitAi.interview.entity.AnswerAnalysis;
import com.backend.recruitAi.interview.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerAnalysisRepository extends JpaRepository<AnswerAnalysis, Long> {
    List<AnswerAnalysis> findByInterviewResult(InterviewResult interviewResult);

    Optional<AnswerAnalysis> findByIdAndInterviewResult(Long id, InterviewResult interviewResult);
}
