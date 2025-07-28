package com.backend.recruitAi.member.repository;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}