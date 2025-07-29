package com.backend.recruitAi.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 & 인가
    UNAUTHORIZED(401, "AUTH001", "비밀번호가 일치하지 않습니다."),
    FORBIDDEN(403, "AUTH002", "접근 권한이 없습니다."),
    ACCESS_TOKEN_NOT_FOUND(401, "AUTH003", "Access Token이 없습니다."),
    COOKIE_NOT_FOUND(401, "AUTH004", "쿠키가 없습니다."),
    INVALID_REFRESH_TOKEN(401, "AUTH005", "Refresh Token이 유효하지 않거나 존재하지 않습니다."),

    // 회원 관련
    USER_NOT_FOUND(404, "USER001", "회원이 존재하지 않습니다."),
    EMAIL_DUPLICATED(400, "EMAIL001", "이메일이 이미 존재합니다."),
    EMAIL_VERIFICATION_NOT_FOUND(400, "EMAIL002", "이메일 인증 정보가 존재하지 않습니다."),
    INVALID_VERIFICATION_CODE(400, "EMAIL003", "이메일 인증 코드가 올바르지 않습니다."),
    EMAIL_ALREADY_VERIFIED(400, "EMAIL004", "이미 인증된 이메일입니다."),
    EMAIL_CODE_EXPIRED(400, "EMAIL005", "이메일 인증 코드가 만료되었습니다."),
    MAIL_SEND_FAILED(500, "EMAIL006", "이메일 전송 중 오류가 발생했습니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(500, "COMMON001", "서버 오류입니다.");

    private final int status;
    private final String code;
    private final String message;
}