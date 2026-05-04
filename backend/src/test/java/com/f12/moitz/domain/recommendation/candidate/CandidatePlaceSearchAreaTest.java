package com.f12.moitz.domain.recommendation.candidate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidatePlaceSearchAreaTest {

    @Test
    @DisplayName("출발지들의 중심 좌표와 검색 반경을 가진다")
    void constructor() {
        final CandidatePlaceSearchArea searchArea = new CandidatePlaceSearchArea(
                List.of(
                        new Place("강남역", new Point(127.0, 37.0)),
                        new Place("역삼역", new Point(129.0, 39.0))
                ),
                10
        );

        assertThat(searchArea.getCenter()).isEqualTo(new Point(128.0, 38.0));
        assertThat(searchArea.getRadiusKilometers()).isEqualTo(10);
    }

    @Test
    @DisplayName("출발지 목록은 비어있거나 null일 수 없다")
    void constructor_ValidateOriginPlaces() {
        assertThatThrownBy(() -> new CandidatePlaceSearchArea(null, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발 지하철역 목록은 비어있거나 null일 수 없습니다.");
        assertThatThrownBy(() -> new CandidatePlaceSearchArea(List.of(), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발 지하철역 목록은 비어있거나 null일 수 없습니다.");
    }

    @Test
    @DisplayName("출발지 목록에 null이 포함될 수 없다")
    void constructor_ValidateNullOriginPlace() {
        assertThatThrownBy(() -> new CandidatePlaceSearchArea(Collections.singletonList(null), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발 지하철역 목록에 null이 포함될 수 없습니다.");
    }

    @Test
    @DisplayName("검색 반경은 0보다 커야 한다")
    void constructor_ValidateRadiusKilometers() {
        final Place place = new Place("강남역", new Point(127.0, 37.0));

        assertThatThrownBy(() -> new CandidatePlaceSearchArea(List.of(place), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("후보역 검색 반경은 0보다 커야 합니다.");
        assertThatThrownBy(() -> new CandidatePlaceSearchArea(List.of(place), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("후보역 검색 반경은 0보다 커야 합니다.");
    }

}
