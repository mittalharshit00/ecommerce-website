package com.ecommerce.platform.exception;

public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}