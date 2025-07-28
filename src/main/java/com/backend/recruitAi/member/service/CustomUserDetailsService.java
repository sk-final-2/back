package com.backend.recruitAi.member.service;

import com.backend.recruitAi.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return new CustomUserDetails(
            memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음")));
    }
}