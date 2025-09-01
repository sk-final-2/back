package com.backend.recruitAi.member.controller;

import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.member.dto.MemberListDto;
import com.backend.recruitAi.member.dto.MemberStatusDto;
import com.backend.recruitAi.member.dto.SuspendRequestDto;
import com.backend.recruitAi.member.dto.UserStatusDto;
import com.backend.recruitAi.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')") // ✅ 모든 엔드포인트에 관리자 권한 필수
public class AdminController {
    private final AdminService adminService;

    // 회원 목록 조회 및 검색
    @GetMapping("/members")
    public ResponseDto<Page<MemberListDto>> getMembers(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        Page<MemberListDto> members = adminService.getAllMembers(query, pageable);
        return ResponseDto.success(members);
    }

    // ✅ 추가: 회원 삭제
    @DeleteMapping("/members/{memberId}")
    public ResponseDto<?> deleteMember(@PathVariable Long memberId) {
        adminService.deleteMember(memberId);
        return ResponseDto.success("회원 삭제 성공");
    }

    // ✅ 수정: 회원 정지 API가 DTO를 받도록 변경
    @PostMapping("/members/{memberId}/suspend")
    public ResponseDto<?> suspendMember(@PathVariable Long memberId, @RequestBody SuspendRequestDto request) {
        adminService.suspendMember(memberId, request.getReason(),request.getSuspendedUntil());
        return ResponseDto.success("회원 정지 처리 성공");
    }

    // ✅ 추가: 회원 정지 해제
    @PostMapping("/members/{memberId}/unsuspend")
    public ResponseDto<?> unSuspendMember(@PathVariable Long memberId) {
        adminService.unSuspendMember(memberId);
        return ResponseDto.success("회원 정지 해제 성공");
    }

    // 일별 가입자 수 통계 API
    @GetMapping("stats/daily-users")
    public ResponseDto<List<UserStatusDto>> getDailyUserStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<UserStatusDto> stats = adminService.getDailyUserStats(startDate, endDate);
        return ResponseDto.success(stats);
    }

    // 월별 가입자 수 통계 API
    @GetMapping("stats/monthly-users")
    public ResponseDto<List<UserStatusDto>> getMonthlyUserStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<UserStatusDto> stats = adminService.getMonthlyUserStats(startDate, endDate);
        return ResponseDto.success(stats);
    }

    // 회원 상태 통계 API
    @GetMapping("/stats/member-status")
    public ResponseDto<MemberStatusDto> getMemberStatusStats() {
        MemberStatusDto stats = adminService.getMemberStatusStats();
        return ResponseDto.success(stats);
    }
}
