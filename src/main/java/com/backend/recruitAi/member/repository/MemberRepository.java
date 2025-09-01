package com.backend.recruitAi.member.repository;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import com.backend.recruitAi.member.entity.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    // ✅ 추가: 이메일 또는 이름으로 회원 검색
    List<Member> findByEmailContainingOrNameContaining(String email, String name);

    // ✅ 일별 가입자 수 통계 쿼리 (LocalDate 타입)
    @Query("SELECT DATE(m.createdAt) as date, COUNT(m.id) as count " +
            "FROM Member m " +
            "WHERE DATE(m.createdAt) BETWEEN :startDate AND :endDate " +
            "GROUP BY date " +
            "ORDER BY date ASC")
    List<Object[]> countByDailyCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ✅ 월별 가입자 수 통계 쿼리 (LocalDateTime 타입)
    @Query("SELECT SUBSTRING(CAST(m.createdAt AS string), 1, 7) as month, COUNT(m.id) as count " +
            "FROM Member m " +
            "WHERE m.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY month " +
            "ORDER BY month ASC")
    List<Object[]> countByMonthlyCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    long countBySuspended(boolean suspended);

    Page<Member> findByEmailContainingOrNameContaining(String email, String name, Pageable pageable);
    Page<Member> findAll(Pageable pageable); // ✅ 추가: Page를 반환하도록 오버라이드
}