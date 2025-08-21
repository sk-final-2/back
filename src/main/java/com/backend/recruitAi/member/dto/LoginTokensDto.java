package com.backend.recruitAi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginTokensDto {
    private String accessToken;
    private String rtid; // 또는 refreshToken
    private LoginResponseDto profile;
}