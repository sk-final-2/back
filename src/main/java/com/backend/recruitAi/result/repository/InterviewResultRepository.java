package com.backend.recruitAi.result.repository;

import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
    List<InterviewResult> findByInterview(Interview interview);
    List<InterviewResult> findAllByInterview(Interview interview);
}