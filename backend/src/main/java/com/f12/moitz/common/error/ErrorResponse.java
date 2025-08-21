package com.f12.moitz.common.error;

import com.f12.moitz.common.error.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Schema(description = "오류 응답")
@Getter
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int status;

    @Schema(description = "오류 코드", example = "INVALID_REQUEST")
    private final String code;

    @Schema(description = "오류 메시지", example = "잘못된 요청입니다.")
    private final String message;

    @Schema(description = "요청 메서드", example = "POST")
    private final String method;

    @Schema(description = "요청 경로", example = "/locations")
    private final String path;

    @Schema(description = "오류 발생 시간", example = "2023-10-01T12:00:00")
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String code, String message, String method, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.method = method;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, ErrorCode errorCode, String method, String path) {
        this.status = status;
        this.code = errorCode.getCode();
        this.message = errorCode.getClientMessage();
        this.method = method;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

}
