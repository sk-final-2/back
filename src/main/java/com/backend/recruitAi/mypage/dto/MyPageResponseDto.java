package com.backend.recruitAi.mypage.dto;

import com.backend.recruitAi.member.entity.GenderType;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import com.backend.recruitAi.member.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MyPageResponseDto {
    private Long id;
    private String email;
    private String name;
    private String postcode;
    private String address1;
    private String address2;
    private GenderType gender;
    private LocalDate birth;
    private Provider provider;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // 수정된 필드명으로 변경

    public static MyPageResponseDto fromEntity(Member member) {
        return MyPageResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .postcode(member.getPostcode())
                .address1(member.getAddress1())
                .address2(member.getAddress2())
                .gender(member.getGender())
                .birth(member.getBirth())
                .provider(member.getProvider())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}