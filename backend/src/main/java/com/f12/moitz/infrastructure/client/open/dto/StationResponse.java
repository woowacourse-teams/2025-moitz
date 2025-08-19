package com.f12.moitz.infrastructure.client.open.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StationResponse(
        String stnNm,
        String lineNm
) {

}
