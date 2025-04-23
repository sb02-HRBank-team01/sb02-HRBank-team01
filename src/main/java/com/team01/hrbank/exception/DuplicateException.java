package com.team01.hrbank.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super("이미 존재 하는 데이터 입니다. : " + message);
    }
}
