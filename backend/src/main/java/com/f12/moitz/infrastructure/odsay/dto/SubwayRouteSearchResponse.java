package com.f12.moitz.infrastructure.odsay.dto;

import java.util.Optional;

public record SubwayRouteSearchResponse(
        ResultResponse result,
        Optional<OdsayErrorResponse> error
) {

}
