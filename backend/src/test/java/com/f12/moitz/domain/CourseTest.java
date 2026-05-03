package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    @DisplayName("이동 코스의 좌표 목록은 null이거나 비어있거나 null 좌표를 포함할 수 없다")
    void constructor_ThrowsExceptionWhenPointsAreInvalid() {
        final Point point = new Point(127.0, 37.0);

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Course(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스의 좌표 목록은 필수입니다.");
            softAssertions.assertThatThrownBy(() -> new Course(List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스의 좌표 목록은 필수입니다.");
            softAssertions.assertThatThrownBy(() -> new Course(Arrays.asList(point, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스의 좌표 목록에 null이 포함될 수 없습니다.");
        });
    }

    @Test
    @DisplayName("이동 코스의 좌표 목록은 외부에서 변경할 수 없다")
    void getPoints_ReturnsUnmodifiableList() {
        final List<Point> points = new ArrayList<>();
        points.add(new Point(127.0, 37.0));

        final Course course = new Course(points);

        points.clear();

        assertThat(course.getPoints()).hasSize(1);
        assertThatThrownBy(() -> course.getPoints().add(new Point(127.1, 37.1)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

}
