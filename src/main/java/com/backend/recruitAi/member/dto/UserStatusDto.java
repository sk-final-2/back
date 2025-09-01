package com.backend.recruitAi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class UserStatusDto {
    private String date; // 날짜 (yyyy-MM-dd 또는 yyyy-MM 형식)
    private Long count;  // 가입자 수
}
