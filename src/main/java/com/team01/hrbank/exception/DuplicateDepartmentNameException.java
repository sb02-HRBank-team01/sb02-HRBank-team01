package com.team01.hrbank.exception;

public class DuplicateDepartmentNameException extends RuntimeException {
    public DuplicateDepartmentNameException(String name) {
        super("이미 존재하는 부서명입니다: " + name);
    }
}
