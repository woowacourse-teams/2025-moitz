package com.f12.moitz.infrastructure.client.kakao.dto;

import lombok.Getter;

@Getter
public class SearchImageRequest{

    private final String query;
    private final int page;
    private final int size;

    public SearchImageRequest(String stationName, String place, int page, int size) {
        this.query = stationName + " " + place;
        this.page = page;
        this.size = size;
    }
}
