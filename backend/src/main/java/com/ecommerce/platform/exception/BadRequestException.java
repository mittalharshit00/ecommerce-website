package com.ecommerce.platform.exception;

public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public BadRequestException(String message) {
        super(message);
        this.errorCode = ErrorCode.BAD_REQUEST;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}