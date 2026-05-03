package com.f12.moitz.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedPlacesTest {

    @Test
    @DisplayName("모든 추천 조건에 추천 장소가 있으면 조건을 충족한다")
    void satisfiesAllConditions() {
        // Given
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(Map.of(
                RecommendCondition.CAFE, List.of(createRecommendedPlace("카페")),
                RecommendCondition.RESTAURANT, List.of(createRecommendedPlace("식당"))
        ));

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedPlaces.satisfiesAllConditions(List.of(
                    RecommendCondition.CAFE,
                    RecommendCondition.RESTAURANT
            ))).isTrue();
            softAssertions.assertThat(recommendedPlaces.satisfiesAllConditions(List.of(
                    RecommendCondition.CAFE,
                    RecommendCondition.ACTIVITY
            ))).isFalse();
        });
    }

    @Test
    @DisplayName("추천 조건이 비어있거나 조건별 추천 장소가 비어있으면 조건을 충족하지 않는다")
    void doesNotSatisfyInvalidConditions() {
        // Given
        final RecommendedPlaces emptyRecommendedPlaces = new RecommendedPlaces(Map.of());
        final RecommendedPlaces recommendedPlacesWithEmptyCategory = new RecommendedPlaces(Map.of(
                RecommendCondition.CAFE, List.of()
        ));

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(emptyRecommendedPlaces.satisfiesAllConditions(List.of(RecommendCondition.CAFE))).isFalse();
            softAssertions.assertThat(recommendedPlacesWithEmptyCategory.satisfiesAllConditions(List.of(RecommendCondition.CAFE))).isFalse();
            softAssertions.assertThat(recommendedPlacesWithEmptyCategory.satisfiesAllConditions(List.of())).isFalse();
            softAssertions.assertThat(recommendedPlacesWithEmptyCategory.satisfiesAllConditions(null)).isFalse();
        });
    }

    @Test
    @DisplayName("추천 조건별 추천 장소는 null을 포함할 수 없다")
    void constructor_ThrowsExceptionWhenPlacesByConditionContainNull() {
        final Map<RecommendCondition, List<RecommendedPlace>> nullConditionMap = new LinkedHashMap<>();
        nullConditionMap.put(null, List.of(createRecommendedPlace("카페")));

        final Map<RecommendCondition, List<RecommendedPlace>> nullPlacesMap = new LinkedHashMap<>();
        nullPlacesMap.put(RecommendCondition.CAFE, null);

        final Map<RecommendCondition, List<RecommendedPlace>> nullPlaceMap = new LinkedHashMap<>();
        nullPlaceMap.put(RecommendCondition.CAFE, new ArrayList<>());
        nullPlaceMap.get(RecommendCondition.CAFE).add(null);

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new RecommendedPlaces(nullConditionMap))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 조건은 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new RecommendedPlaces(nullPlacesMap))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록은 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new RecommendedPlaces(nullPlaceMap))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록에 null이 포함될 수 없습니다.");
        });
    }

    @Test
    @DisplayName("추천 조건별 추천 장소는 외부에서 변경할 수 없다")
    void getPlacesByCondition_ReturnsUnmodifiableMapAndLists() {
        final List<RecommendedPlace> cafes = new ArrayList<>();
        cafes.add(createRecommendedPlace("카페"));
        final Map<RecommendCondition, List<RecommendedPlace>> placesByCondition = new LinkedHashMap<>();
        placesByCondition.put(RecommendCondition.CAFE, cafes);

        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(placesByCondition);

        cafes.clear();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedPlaces.satisfiesAllConditions(List.of(RecommendCondition.CAFE))).isTrue();
            softAssertions.assertThatThrownBy(() -> recommendedPlaces.getPlacesByCondition()
                            .put(RecommendCondition.RESTAURANT, List.of(createRecommendedPlace("식당"))))
                    .isInstanceOf(UnsupportedOperationException.class);
            softAssertions.assertThatThrownBy(() -> recommendedPlaces.getPlacesByCondition()
                            .get(RecommendCondition.CAFE)
                            .add(createRecommendedPlace("다른 카페")))
                    .isInstanceOf(UnsupportedOperationException.class);
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
