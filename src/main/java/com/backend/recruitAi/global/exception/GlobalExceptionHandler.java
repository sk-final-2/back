package com.backend.recruitAi.global.exception;

import com.backend.recruitAi.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(ResponseDto.error(ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleUnexpectedException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
