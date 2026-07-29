package com.ecommerce.platform.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(
                        HttpStatus.CONFLICT,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(
                        HttpStatus.UNAUTHORIZED,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(
                        HttpStatus.FORBIDDEN,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {

            validationErrors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        ErrorCode.VALIDATION_ERROR,
                        request.getRequestURI(),
                        validationErrors
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors = new HashMap<>();

        for (ConstraintViolation<?> violation :
                exception.getConstraintViolations()) {

            String propertyPath =
                    violation.getPropertyPath().toString();

            validationErrors.put(
                    propertyPath,
                    violation.getMessage()
            );
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        ErrorCode.VALIDATION_ERROR,
                        request.getRequestURI(),
                        validationErrors
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(
                        HttpStatus.FORBIDDEN,
                        "Access denied",
                        ErrorCode.FORBIDDEN,
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid or expired token",
                        ErrorCode.UNAUTHORIZED,
                        request.getRequestURI(),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred",
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        request.getRequestURI(),
                        null
                ));
    }

    private ErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            ErrorCode errorCode,
            String path,
            Map<String, String> validationErrors
    ) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}