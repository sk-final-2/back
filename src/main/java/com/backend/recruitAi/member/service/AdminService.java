package com.backend.recruitAi.member.service;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.member.dto.MemberListDto;
import com.backend.recruitAi.member.dto.MemberStatusDto;
import com.backend.recruitAi.member.dto.UserStatusDto;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final MemberRepository memberRepository;

    // 전체 회원 목록 조회 및 검색
    public Page<MemberListDto> getAllMembers(String query, Pageable pageable) {
        Page<Member> memberPage;

        pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());

        if (query != null && !query.trim().isEmpty()) {
            memberPage = memberRepository.findByEmailContainingOrNameContaining(query, query, pageable);
        } else {
            memberPage = memberRepository.findAll(pageable);
        }

        return memberPage.map(MemberListDto::fromEntity);
    }

    // ✅ 추가: 회원 탈퇴 처리 (실제 계정 삭제)
    @Transactional
    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        memberRepository.deleteById(memberId);
    }

    // ✅ 추가: 회원 정지 기능
    @Transactional
    public void suspendMember(Long memberId, String reason, LocalDate suspendedUntil) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        member.setSuspended(true);
        member.setSuspendedReason(reason);
        member.setSuspendedUntil(suspendedUntil.atStartOfDay());
        memberRepository.save(member);
    }

    // ✅ 추가: 회원 정지 해제 기능
    @Transactional
    public void unSuspendMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        member.setSuspended(false);
        memberRepository.save(member);
    }

    // ✅ 일별 통계 로직: 쿼리 결과를 UserStatsDto 리스트로 변환
    public List<UserStatusDto> getDailyUserStats(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = memberRepository.countByDailyCreatedAtBetween(startDate, endDate);

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(result -> new UserStatusDto(result[0].toString(), (Long) result[1]))
                .collect(Collectors.toList());
    }

    // ✅ 월별 통계 로직: 쿼리 결과를 UserStatsDto 리스트로 변환
    public List<UserStatusDto> getMonthlyUserStats(LocalDate startDate, LocalDate endDate) {
        // LocalDate를 LocalDateTime으로 변환하여 쿼리에 사용
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> results = memberRepository.countByMonthlyCreatedAtBetween(startDateTime, endDateTime);

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream()
                .map(result -> new UserStatusDto(result[0].toString(), (Long) result[1]))
                .collect(Collectors.toList());
    }

    // ✅ 회원 상태 통계 로직 (신규)
    public MemberStatusDto getMemberStatusStats() {
        long totalMembers = memberRepository.count();
        long suspendedMembers = memberRepository.countBySuspended(true);
        long activeMembers = totalMembers - suspendedMembers;
        return new MemberStatusDto(activeMembers, suspendedMembers);
    }
}
