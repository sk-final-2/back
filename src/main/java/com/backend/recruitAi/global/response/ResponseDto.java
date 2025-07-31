package com.backend.recruitAi.global.response;

import com.backend.recruitAi.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseDto<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    // 성공 응답
    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .status(HttpStatus.OK.value()) // 200 -> HttpStatus상수활용(가독성향상)
                .code("SUCCESS")
                .message("success")
                .data(data)
                .build();
    }

    //OCR 성공 응답 (data를 같이 넘기는 구조)
    public static <T> ResponseDto<T> success(String message, T data) {
        return ResponseDto.<T>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답
    public static <T> ResponseDto<T> error(ErrorCode errorCode) {
        return ResponseDto.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }
}