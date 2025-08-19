package com.f12.moitz.infrastructure.client.odsay.dto;

public record OdsayErrorResponse(
        String code,
        String msg,
        String message
) {

}
