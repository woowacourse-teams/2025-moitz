package com.f12.moitz.infrastructure.client.kakao.dto;

import lombok.Getter;

@Getter
public class SearchImageRequest{

    private final String query;
    private final int page;
    private final int size;

    public SearchImageRequest(final String stationName, final String place, final int page, final int size) {
        this.query = stationName + " " + place;
        this.page = page;
        this.size = size;
    }
}
