package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.place.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedPlaceTest {

    private static final Point DEFAULT_POINT = new Point(127.0, 37.5);

    @Test
    @DisplayName("예외가 발생하지 않고 추천 장소가 생성된다")
    void doesNotThrow() {
        // Given
        final String placeName = "스타벅스";
        final String category = "카페";
        final int walkingTime = 5;
        final String url = "https://www.starbucks.co.kr";
        final String imageUrl = "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966";

        // When & Then
        assertThatNoException().isThrownBy(
                () -> new RecommendedPlace(placeName, DEFAULT_POINT, category, walkingTime, url, imageUrl));
    }

    @Test
    @DisplayName("필수 인자가 null이거나 비어있다면 추천 장소를 생성할 수 없다")
    void isThrownByInvalidArguments() {
        // Given
        final String placeName = "스타벅스";
        final String category = "카페";
        final int walkingTime = 5;
        final String url = "https://www.starbucks.co.kr";
        final String imageUrl = "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(
                            () -> new RecommendedPlace(null, DEFAULT_POINT, category, walkingTime, url, imageUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 비어있거나 null일 수 없습니다.");

            softAssertions.assertThatThrownBy(
                            () -> new RecommendedPlace(placeName, DEFAULT_POINT, null, walkingTime, url, imageUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다.");

            softAssertions.assertThatThrownBy(
                            () -> new RecommendedPlace(placeName, DEFAULT_POINT, "", walkingTime, url, imageUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다.");

            softAssertions.assertThatThrownBy(
                            () -> new RecommendedPlace(placeName, DEFAULT_POINT, category, walkingTime, null, imageUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("URL은 필수입니다.");

            softAssertions.assertThatThrownBy(
                            () -> new RecommendedPlace(placeName, DEFAULT_POINT, category, walkingTime, "", imageUrl))
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
        final String imageUrl = "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966";

        // When & Then
        assertThatThrownBy(() -> new RecommendedPlace(placeName, DEFAULT_POINT, category, walkingTime, url, imageUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("도보 시간은 0 이상이어야 합니다.");
    }

}
