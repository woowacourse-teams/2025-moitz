package com.f12.moitz.common.error.exception;

public enum ExternalApiErrorCode implements ErrorCode {

    // GEMINI API 예외 E000
    INVALID_GEMINI_API_KEY("E0001", "유효하지 않은 GEMINI API KEY입니다.", false),
    INVALID_GEMINI_API_RESPONSE("E0002", "GEMINI API 응답이 정상적으로 생성되지 않았습니다.", false),
    INVALID_GEMINI_RESPONSE_FORMAT("E0003", "GEMINI API 응답이 예측한 형식이 아닙니다.", true),
    GEMINI_API_SERVER_UNAVAILABLE("E0004", "GEMINI API 서버가 일시적으로 응답하지 않습니다.", true),
    EXCEEDED_GEMINI_API_TOKEN_QUOTA("E0005", "GEMINI API 토큰 사용량이 초과되었습니다.", false),
    GEMINI_API_SERVER_UNRESPONSIVE("E0006", "GEMINI API 서버가 응답하지 않습니다.", false),

    // ODSAY API 예외 E001
    INVALID_ODSAY_API_KEY("E0011", "유효하지 않은 ODSAY API KEY입니다.", false),
    INVALID_ODSAY_API_RESPONSE("E0012", "ODSAY API 응답이 정상적으로 생성되지 않았습니다.", false),
    EXCEEDED_ODSAY_API_TOKEN_QUOTA("E0013", "ODSAY API 토큰 사용량이 초과되었습니다.", false),
    ODSAY_API_SERVER_UNRESPONSIVE("E0014", "ODSAY API 서버가 응답하지 않습니다.", false),
    ODSAY_API_BLOCKED("E0015", "ODDAY API에서 요청을 차단했습니다. 잠시 후 다시 시도해주세요.", true),

    // KAKAO MAP API 예외 E002
    INVALID_KAKAO_MAP_API_KEY("E0021", "유효하지 않은 KAKAO MAP API KEY입니다.", false),
    INVALID_KAKAO_MAP_API_RESPONSE("E0022", "KAKAO MAP API 응답이 정상적으로 생성되지 않았습니다.", false),
    EXCEEDED_KAKAO_MAP_API_TOKEN_QUOTA("E0023", "KAKAO MAP API 토큰 사용량이 초과되었습니다.", false),
    KAKAO_MAP_API_SERVER_UNRESPONSIVE("E0024", "KAKAO MAP API 서버가 응답하지 않습니다.", false);

    private static final String CLIENT_ERROR_MESSAGE = "서버 오류입니다. 관리자에게 문의해주세요.";
    private static final String CLIENT_RETRY_ERROR_MESSAGE = "일시적인 서버 오류입니다. 잠시 후 시도해주세요.";

    private final String code;
    private final String message;
    private final boolean canRetry;

    ExternalApiErrorCode(final String code, final String message, final boolean canRetry) {
        this.code = code;
        this.message = message;
        this.canRetry = canRetry;
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
        if (canRetry) {
            return CLIENT_RETRY_ERROR_MESSAGE;
        }
        return CLIENT_ERROR_MESSAGE;
    }

}
