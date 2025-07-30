package com.backend.recruitAi.member.service;

import com.backend.recruitAi.member.dto.KakaoSignupRequest;
import com.backend.recruitAi.member.dto.GoogleSignupRequest;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import com.backend.recruitAi.member.entity.Role;
import com.backend.recruitAi.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member kakaoSignup(KakaoSignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode("kakao-user")) // 의미 없는 패스워드
                .name(request.getName())
                .gender(request.getGender())
                .birth(request.getBirth())
                .postcode(request.getZipcode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .provider(Provider.KAKAO)
                .role(Role.ROLE_USER)
                .build();

        return memberRepository.save(member);
    }

    public Member googleSignup(GoogleSignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode("google-user")) // 의미 없는 패스워드
                .name(request.getName())
                .gender(request.getGender())
                .birth(request.getBirth())
                .postcode(request.getZipcode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .provider(Provider.GOOGLE)
                .role(Role.ROLE_USER)
                .build();

        return memberRepository.save(member);
    }

}
