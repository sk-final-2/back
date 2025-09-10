package com.backend.recruitAi.interview.repository;

import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByMemberId(Long memberId);

    @EntityGraph(attributePaths = {"answerAnalyses"})
    Optional<Interview> findByIdAndMemberId(Long id, Long memberId);

    Optional<Interview> findByUuid(String uuid);

    @Query(value = "WITH RECURSIVE dates AS ( " +
            "    SELECT :startDate AS date " +
            "    UNION ALL " +
            "    SELECT DATE_ADD(date, INTERVAL 1 DAY) FROM dates WHERE date < :endDate " +
            ") " +
            "SELECT " +
            "    DATE_FORMAT(d.date, '%Y-%m-%d') AS date, " +
            "    COALESCE(COUNT(i.created_at), 0) AS count " +
            "FROM dates d " +
            "LEFT JOIN interview i ON DATE(i.created_at) = d.date " +
            "GROUP BY d.date " +
            "ORDER BY d.date ASC",
            nativeQuery = true)
    List<Object[]> countDailyInterviews(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query(value = "WITH RECURSIVE months AS ( " +
            "    SELECT :startDate AS month " +
            "    UNION ALL " +
            "    SELECT DATE_ADD(month, INTERVAL 1 MONTH) FROM months WHERE month < :endDate " +
            ") " +
            "SELECT " +
            "    DATE_FORMAT(m.month, '%Y-%m') AS date, " +
            "    COALESCE(COUNT(i.created_at), 0) AS count " +
            "FROM months m " +
            "LEFT JOIN interview i ON DATE_FORMAT(i.created_at, '%Y-%m') = DATE_FORMAT(m.month, '%Y-%m') " +
            "GROUP BY m.month " +
            "ORDER BY m.month ASC",
            nativeQuery = true)
    List<Object[]> countMonthlyInterviews(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}