package com.backend.recruitAi.interview.repository;

import com.backend.recruitAi.interview.entity.InterviewResult;
import com.backend.recruitAi.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
    List<InterviewResult> findByMember(Member member);

    Optional<InterviewResult> findByIdAndMember(Long id, Member member);
}
