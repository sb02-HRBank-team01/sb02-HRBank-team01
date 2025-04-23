package com.team01.hrbank.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + "을(를) 찾을 수 없습니다. ID: " + id);
    }
}
