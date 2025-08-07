package com.f12.moitz.infrastructure.odsay.dto;

import java.util.List;
import java.util.Optional;

public record SubwayRouteSearchResponse(
        ResultResponse result,
        Optional<List<OdsayErrorResponse>> error
) {

}
