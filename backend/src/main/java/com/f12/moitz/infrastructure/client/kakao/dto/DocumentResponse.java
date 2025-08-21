package com.f12.moitz.infrastructure.client.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DocumentResponse(
        @JsonProperty("category_group_name") String categoryGroupName,
        @JsonProperty("category_name") String categoryName,
        String distance,
        @JsonProperty("place_name") String placeName,
        @JsonProperty("place_url") String placeUrl,
        String x,
        String y
) {

}
