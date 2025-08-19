package com.f12.moitz.infrastructure.client.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MetaResponse(
        @JsonProperty("is_end") boolean isEnd,
        @JsonProperty("pageable_count") int pageableCount,
        @JsonProperty("total_count") int totalCount,
        @JsonProperty("same_name") Object sameName
) {

}
