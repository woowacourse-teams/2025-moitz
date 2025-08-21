package com.f12.moitz.domain;

import lombok.Getter;

@Getter
public class RecommendedPlace {

    private final String placeName;
    private final String category;
    private final int walkingTime;
    private final String url;

    public RecommendedPlace(final String placeName, final String category, final int walkingTime, final String url) {
        validate(placeName, category, walkingTime, url);
        this.placeName = placeName;
        this.category = category;
        this.walkingTime = walkingTime;
        this.url = url;
    }

    private void validate(final String placeName, final String category, final int walkingTime, final String url) {
        if (placeName == null) {
            throw new IllegalArgumentException("장소 이름은 필수입니다.");
        }
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
        if (walkingTime < 0) {
            throw new IllegalArgumentException("도보 시간은 0 이상이어야 합니다.");
        }
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL은 필수입니다.");
        }
    }

}
