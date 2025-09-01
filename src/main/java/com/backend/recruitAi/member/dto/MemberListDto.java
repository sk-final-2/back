package com.backend.recruitAi.member.dto;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Role;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MemberListDto {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isSuspended;
    private LocalDateTime suspendedUntil; // ✅ 이 라인을 추가하세요.
    private String suspendedReason;

    public static MemberListDto fromEntity(Member member) {
        return MemberListDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .isSuspended(member.isSuspended()) // ✅ 이 라인을 추가하세요.
                .suspendedUntil(member.getSuspendedUntil())
                .suspendedReason(member.getSuspendedReason())
                .build();
    }
}