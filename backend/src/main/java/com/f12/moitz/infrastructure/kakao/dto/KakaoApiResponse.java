package com.f12.moitz.infrastructure.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record KakaoApiResponse(
        List<Document> documents,
        Meta meta
) {

    private Document findStation() {
        return documents.stream()
                .filter(document -> "지하철역".equals(document.categoryGroupName()))
                .findFirst()
                .orElse(null);
    }

    public String findStationPlaceName() {
        return findStation().placeName();
    }

    public double findStationX() {
        return Double.parseDouble(findStation().x());
    }

    public double findStationY() {
        return Double.parseDouble(findStation().y());
    }

    public int totalCount() {
        return meta().totalCount();
    }

}

record Document(
        @JsonProperty("address_name") String addressName,
        @JsonProperty("category_group_code") String categoryGroupCode,
        @JsonProperty("category_group_name") String categoryGroupName,
        @JsonProperty("category_name") String categoryName,
        String distance,
        String id,
        String phone,
        @JsonProperty("place_name") String placeName,
        @JsonProperty("place_url") String placeUrl,
        @JsonProperty("road_address_name") String roadAddressName,
        String x,
        String y
) {

}

record Meta(
        @JsonProperty("is_end") boolean isEnd,
        @JsonProperty("pageable_count") int pageableCount,
        @JsonProperty("total_count") int totalCount,
        @JsonProperty("same_name") Object sameName
) {

}
