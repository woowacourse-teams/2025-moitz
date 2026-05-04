package com.f12.moitz.domain.recommendation;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RecommendedPlace extends Place {

    private String category;
    private int walkingTime;
    private String placeUrl;
    private String imageUrl;

    public RecommendedPlace(
            final String placeName,
            final Point point,
            final String category,
            final int walkingTime,
            final String placeUrl,
            final String imageUrl
    ) {
        super(placeName, point);
        validate(category, walkingTime, placeUrl);
        this.category = category;
        this.walkingTime = walkingTime;
        this.placeUrl = placeUrl;
        this.imageUrl = imageUrl;
    }

    private void validate(final String category, final int walkingTime, final String url) {
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

    public double getX() {
        return this.getPoint().getX();
    }

    public double getY() {
        return this.getPoint().getY();
    }

}
