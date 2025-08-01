package com.backend.recruitAi.result.repository;

import com.backend.recruitAi.result.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {

    List<InterviewResult> findAllByMemberId(Long memberId);
    Optional<InterviewResult> findByIdAndMemberId(Long id, Long memberId);
}