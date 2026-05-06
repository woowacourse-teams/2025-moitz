package com.f12.moitz.common.error.exception;

public enum GeneralErrorCode implements ErrorCode {

    // CLIENT 예외 C000
    INPUT_INVALID_START_LOCATION("C0001", "유효하지 않은 출발지입니다."),
    INPUT_INVALID_ARRIVAL_TIME("C0002", "유효하지 않은 도착시간입니다."),
    INPUT_INVALID_DESCRIPTION("C0003", "유효하지 않은 성격입니다."),
    INPUT_INVALID_RESULT("C0004", "존재하지 않거나 유효 기간이 만료된 추천 결과입니다."),
    INPUT_INVALID_CANDIDATE_NAME("C0005", "유효하지 않은 추천 지역입니다."),
    RECOMMENDATION_NOT_FOUND("C0006", "조건에 맞는 추천 결과를 찾지 못했습니다."),
    INPUT_INVALID_RECOMMENDATION_TAG("C0007", "유효하지 않은 추천 태그입니다."),
    INPUT_INVALID_RECOMMEND_CONDITION("C0008", "유효하지 않은 추천 조건입니다.");

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
