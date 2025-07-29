package com.backend.recruitAi.email.repository;

import com.backend.recruitAi.email.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    Optional<EmailVerification> findByEmail(String email);
}