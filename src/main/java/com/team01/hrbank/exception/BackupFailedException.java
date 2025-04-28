package com.team01.hrbank.exception;

public class BackupFailedException extends RuntimeException {

    public BackupFailedException(String message, Throwable cause) {
        super(message, cause);

    }
    public BackupFailedException(String message) {
        super(message);

    }
}