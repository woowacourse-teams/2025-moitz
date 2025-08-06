package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceTest {

    @Test
    @DisplayName("예외가 발생하지 않고 장소가 생성된다")
    void doesNotThrow() {
        // Given
        final String name = "루터회관";
        final Point point = new Point(125.127, 34.578);

        // When & Then
        assertThatNoException().isThrownBy(() -> new Place(name, point));
    }

    @Test
    @DisplayName("이름이 null이거나 비어있다면 장소를 생성할 수 없다")
    void isThrownByInvalidName() {
        // Given
        final String nameBlank = "";
        final String nameNull = null;
        final Point point = new Point(125.127, 34.578);

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Place(nameBlank, point))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 비어있거나 null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Place(nameNull, point))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 비어있거나 null일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("좌표가 null이라면 장소를 생성할 수 없다")
    void throwExceptionByPointIsNull() {
        // Given
        final String name = "반포 자이";
        final Point point = null;

        // When & Then
        assertThatThrownBy(() -> new Place(name, point))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("좌표는 필수입니다.");
    }

}
