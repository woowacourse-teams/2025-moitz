package com.f12.moitz.common.error.exception;

import lombok.Getter;

@Getter
public class SubwayRouteException extends RuntimeException {

    private final ErrorCode errorCode;

    public SubwayRouteException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SubwayRouteException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
