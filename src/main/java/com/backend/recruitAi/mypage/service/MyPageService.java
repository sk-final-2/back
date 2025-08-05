package com.backend.recruitAi.mypage.service;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.mypage.dto.MyPageUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateMyInfo(Long memberId, MyPageUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 주소 업데이트
        if (requestDto.getPostcode() != null && !requestDto.getPostcode().isEmpty()) {
            member.setPostcode(requestDto.getPostcode());
        }
        if (requestDto.getAddress1() != null && !requestDto.getAddress1().isEmpty()) {
            member.setAddress1(requestDto.getAddress1());
        }
        if (requestDto.getAddress2() != null && !requestDto.getAddress2().isEmpty()) {
            member.setAddress2(requestDto.getAddress2());
        }

        // 비밀번호 업데이트
        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }
    }
}
