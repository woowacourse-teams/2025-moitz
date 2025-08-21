package com.f12.moitz.infrastructure.client.open.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BodyResponse(
        String searchType,
        int totalDstc,
        @JsonProperty("totalreqHr")
        int totalReqHr,
        int totalCardCrg,
        int trsitNmtm,
        List<TransferStationResponse> trfstnNms,
        List<PathResponse> paths
) {

}
