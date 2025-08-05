package com.backend.recruitAi.mypage.controller;

import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.service.CustomUserDetails;
import com.backend.recruitAi.mypage.dto.MyPageResponseDto;
import com.backend.recruitAi.mypage.dto.MyPageSimpleResponseDto;
import com.backend.recruitAi.mypage.dto.MyPageUpdateRequestDto;
import com.backend.recruitAi.mypage.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 내 정보 조회 API
    @GetMapping
    public ResponseEntity<ResponseDto<MyPageResponseDto>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = myPageService.findMemberById(userDetails.getMember().getId());
        return ResponseEntity.ok(ResponseDto.success(MyPageResponseDto.fromEntity(member)));
    }

    // 내 정보 (이메일,이름) 간편 조회 API
    @GetMapping("/info")
    public ResponseEntity<ResponseDto<MyPageSimpleResponseDto>> getMySimpleInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = myPageService.findMemberById(userDetails.getMember().getId());
        return ResponseEntity.ok(ResponseDto.success(MyPageSimpleResponseDto.fromEntity(member)));
    }

    // 내 정보 수정 API
    @PatchMapping
    public ResponseEntity<ResponseDto<String>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MyPageUpdateRequestDto requestDto) {
        myPageService.updateMyInfo(userDetails.getMember().getId(), requestDto);
        return ResponseEntity.ok(ResponseDto.success("회원 정보 수정 성공"));
    }

}