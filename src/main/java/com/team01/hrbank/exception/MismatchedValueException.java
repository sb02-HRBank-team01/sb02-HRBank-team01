package com.team01.hrbank.exception;

public class MismatchedValueException extends RuntimeException {
    public MismatchedValueException(String name) {super("값이 일치하지 않습니다. " + name);}
}
