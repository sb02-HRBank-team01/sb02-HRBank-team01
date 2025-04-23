package com.team01.hrbank.controller.advice;

import com.team01.hrbank.dto.error.ErrorResponse;
import com.team01.hrbank.exception.DuplicateException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 400: 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
            .getFieldError()
            .getDefaultMessage();
        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "잘못된 요청입니다.",
            details
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    // 500: 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "서버 오류입니다.",
            ex.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    // 400: 부서 등록 시 이미 존재하는 부서명이 있을 경우 발생하는 예외 처리
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateException ex) {
        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "잘못된 요청입니다.",
            ex.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
    }
}
