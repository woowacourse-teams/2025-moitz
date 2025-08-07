package com.f12.moitz.common.error.exception;

import lombok.Getter;

@Getter
public class RetryableApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public RetryableApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public RetryableApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
