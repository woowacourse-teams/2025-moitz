package com.f12.moitz.infrastructure.client.odsay.dto;

import com.f12.moitz.infrastructure.client.odsay.dto.deserializer.OdsayErrorDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record SubwayRouteSearchResponse(
        ResultResponse result,
        @JsonDeserialize(using = OdsayErrorDeserializer.class)
        OdsayErrorResponse error
) {

}
