package com.f12.moitz.infrastructure.client.odsay.dto;

import java.util.List;

public record PathResponse(
        int pathType,
        InfoResponse info,
        List<SubPathResponse> subPath
) {

}
