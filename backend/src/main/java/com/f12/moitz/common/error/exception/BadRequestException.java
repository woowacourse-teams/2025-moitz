package com.f12.moitz.common.error.exception;

import java.util.Arrays;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BadRequestException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage() + " " + Arrays.toString(args));
        this.errorCode = errorCode;
    }

    public BadRequestException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessage() + " " + Arrays.toString(args), cause);
        this.errorCode = errorCode;
    }

}
