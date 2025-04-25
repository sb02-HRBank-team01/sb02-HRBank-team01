package com.team01.hrbank.exception;

public class InvalidSortParameterException extends RuntimeException {

    public InvalidSortParameterException(String field) {
        super("유효하지 않은 정렬 파라미터입니다: " + field);
    }
}
