package com.f12.moitz.common.error;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.ExternalApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            final BadRequestException e,
            final HttpServletRequest request
    ) {
        log.warn("BadRequest Exception - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                e.getErrorCode(),
                request.getMethod(),
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(
            final ExternalApiException e,
            final HttpServletRequest request
    ) {
        log.error("ExternalApi Exception - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                e.getErrorCode(),
                request.getMethod(),
                request.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            final Exception e,
            final HttpServletRequest request
    ) {
        log.error("Unexpected Exception - URI '{} {}' ", request.getMethod(), request.getRequestURI(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "I0001",
                e.getMessage(),
                request.getMethod(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
