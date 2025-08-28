package com.backend.recruitAi.interview.repository;

import com.backend.recruitAi.interview.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByMemberId(Long memberId);
    Optional<Interview> findByIdAndMemberId(Long id, Long memberId);
    Optional<Interview> findByUuid(String uuid);
    Page<Interview> findAllByMemberId(Long memberId, Pageable pageable);

}