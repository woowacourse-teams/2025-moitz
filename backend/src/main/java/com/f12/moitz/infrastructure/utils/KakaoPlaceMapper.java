package com.f12.moitz.infrastructure.utils;

import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.recommendation.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class KakaoPlaceMapper {

    private static final String CATEGORY_DELIMITER = ">";
    private static final double WALKING_SPEED_MINUTES_PER_100M = 1.5;

    public RecommendedPlace toRecommendedPlace(final DocumentResponse document) {
        return new RecommendedPlace(
                document.placeName(),
                new Point(
                        Double.parseDouble(document.x()),
                        Double.parseDouble(document.y())
                ),
                parseCategoryName(document.categoryName()),
                calculateWalkingTime(Integer.parseInt(document.distance())),
                document.placeUrl(),
                document.imageUrl()
        );
    }

    private String parseCategoryName(final String categoryName) {
        if (!categoryName.contains(CATEGORY_DELIMITER)) {
            return categoryName;
        }

        final List<String> tokens = Arrays.stream(categoryName.split(CATEGORY_DELIMITER))
                .map(String::trim)
                .toList();

        return tokens.getLast();
    }

    private int calculateWalkingTime(final int distance) {
        return Math.toIntExact(
                Math.round(distance / 100.0 * WALKING_SPEED_MINUTES_PER_100M)
        );
    }

}
