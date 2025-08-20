package com.f12.moitz.infrastructure.client.odsay.dto;

import java.util.List;

public record ResultResponse(
        int subwayCount,
        List<PathResponse> path
) {

}
