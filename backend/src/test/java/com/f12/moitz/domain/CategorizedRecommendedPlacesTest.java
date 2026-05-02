package com.f12.moitz.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategorizedRecommendedPlacesTest {

    @Test
    @DisplayName("모든 추천 조건에 추천 장소가 있으면 조건을 충족한다")
    void satisfiesAll() {
        // Given
        final CategorizedRecommendedPlaces recommendedPlaces = new CategorizedRecommendedPlaces(Map.of(
                RecommendCondition.CAFE, List.of(createRecommendedPlace("카페")),
                RecommendCondition.RESTAURANT, List.of(createRecommendedPlace("식당"))
        ));

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedPlaces.satisfiesAll(List.of(
                    RecommendCondition.CAFE,
                    RecommendCondition.RESTAURANT
            ))).isTrue();
            softAssertions.assertThat(recommendedPlaces.satisfiesAll(List.of(
                    RecommendCondition.CAFE,
                    RecommendCondition.ACTIVITY
            ))).isFalse();
        });
    }

    @Test
    @DisplayName("추천 조건이 비어있거나 조건별 추천 장소가 비어있으면 조건을 충족하지 않는다")
    void doesNotSatisfyInvalidConditions() {
        // Given
        final CategorizedRecommendedPlaces emptyRecommendedPlaces = new CategorizedRecommendedPlaces(Map.of());
        final CategorizedRecommendedPlaces recommendedPlacesWithEmptyCategory = new CategorizedRecommendedPlaces(Map.of(
                RecommendCondition.CAFE, List.of()
        ));

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(emptyRecommendedPlaces.satisfiesAll(List.of(RecommendCondition.CAFE))).isFalse();
            softAssertions.assertThat(recommendedPlacesWithEmptyCategory.satisfiesAll(List.of(RecommendCondition.CAFE))).isFalse();
            softAssertions.assertThat(recommendedPlacesWithEmptyCategory.satisfiesAll(List.of())).isFalse();
            softAssertions.assertThat(recommendedPlacesWithEmptyCategory.satisfiesAll(null)).isFalse();
        });
    }

    private RecommendedPlace createRecommendedPlace(final String name) {
        return new RecommendedPlace(
                name,
                new Point(127.0, 37.0),
                "카테고리",
                10,
                "https://place.test",
                "https://image.test"
        );
    }

}
