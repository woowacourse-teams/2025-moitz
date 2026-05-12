package com.f12.moitz.domain.subway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubwayStationNameTest {

    @Test
    @DisplayName("이수역과 총신대입구역은 총신대입구(이수)역을 먼저 조회한다")
    void getSearchNames_IsuAlias() {
        assertThat(new SubwayStationName("이수역").getSearchNames())
                .containsExactly("총신대입구(이수)역", "이수역");
        assertThat(new SubwayStationName("총신대입구역").getSearchNames())
                .containsExactly("총신대입구(이수)역", "총신대입구역");
    }

    @Test
    @DisplayName("일반 역명은 입력된 이름으로 조회한다")
    void getSearchNames_Default() {
        assertThat(new SubwayStationName("강남역").getSearchNames())
                .containsExactly("강남역");
    }

    @Test
    @DisplayName("지하철역 이름은 비어있거나 null일 수 없다")
    void constructor_ValidateName() {
        assertThatThrownBy(() -> new SubwayStationName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철역 이름은 비어있거나 null일 수 없습니다.");
        assertThatThrownBy(() -> new SubwayStationName(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철역 이름은 비어있거나 null일 수 없습니다.");
    }

}
