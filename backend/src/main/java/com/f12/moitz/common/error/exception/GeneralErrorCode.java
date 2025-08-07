package com.f12.moitz.common.error.exception;

public enum GeneralErrorCode implements ErrorCode {

    // CLIENT 예외 C000
    INPUT_INVALID_START_LOCATION("C0001", "유효하지 않은 출발지입니다."),
    INPUT_INVALID_ARRIVAL_TIME("C0002", "유효하지 않은 도착시간입니다."),
    INPUT_INVALID_DESCRIPTION("C0003", "유효하지 않은 성격입니다.");

    private final String code;
    private final String message;

    GeneralErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getClientMessage() {
        return message;
    }
}
