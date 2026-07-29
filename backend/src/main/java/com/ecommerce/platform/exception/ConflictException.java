package com.ecommerce.platform.exception;

public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConflictException(String message) {
        super(message);
        this.errorCode = ErrorCode.CONFLICT;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}