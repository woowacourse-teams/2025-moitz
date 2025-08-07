package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedPlaceTest {

    @Test
    @DisplayName("예외가 발생하지 않고 추천 장소가 생성된다")
    void doesNotThrow() {
        // Given
        final String placeName = "스타벅스";
        final String category = "카페";
        final int walkingTime = 5;
        final String url = "https://www.starbucks.co.kr";

        // When & Then
        assertThatNoException().isThrownBy(() -> new RecommendedPlace(placeName, category, walkingTime, url));
    }

    @Test
    @DisplayName("필수 인자가 null이거나 비어있다면 추천 장소를 생성할 수 없다")
    void isThrownByInvalidArguments() {
        // Given
        final String placeName = "스타벅스";
        final String category = "카페";
        final int walkingTime = 5;
        final String url = "https://www.starbucks.co.kr";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new RecommendedPlace(null, category, walkingTime, url))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("장소 이름은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new RecommendedPlace(placeName, null, walkingTime, url))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new RecommendedPlace(placeName, "", walkingTime, url))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new RecommendedPlace(placeName, category, walkingTime, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("URL은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new RecommendedPlace(placeName, category, walkingTime, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("URL은 필수입니다.");
        });
    }

    @Test
    @DisplayName("도보 시간이 음수라면 추천 장소를 생성할 수 없다")
    void isThrownByNegativeWalkingTime() {
        // Given
        final String placeName = "스타벅스";
        final String category = "카페";
        final int walkingTime = -5;
        final String url = "https://www.starbucks.co.kr";

        // When & Then
        assertThatThrownBy(() -> new RecommendedPlace(placeName, category, walkingTime, url))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("도보 시간은 0 이상이어야 합니다.");
    }
}
