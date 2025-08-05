package com.backend.recruitAi.mypage.dto;

import com.backend.recruitAi.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyPageSimpleResponseDto {
    private String email;
    private String name;

    public static MyPageSimpleResponseDto fromEntity(Member member) {
        return MyPageSimpleResponseDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }
}
