package com.ecommerce.platform.exception;

public class ForbiddenException extends RuntimeException {

    private final ErrorCode errorCode;

    public ForbiddenException(String message) {
        super(message);
        this.errorCode = ErrorCode.FORBIDDEN;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}