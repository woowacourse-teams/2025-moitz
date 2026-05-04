package com.f12.moitz.domain.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.place.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoursesTest {

    @Test
    @DisplayName("이동 코스 묶음은 null이거나 비어있거나 null 코스를 포함할 수 없다")
    void constructor_ThrowsExceptionWhenCoursesAreInvalid() {
        final Course course = course();

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Courses(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스는 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Courses(List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스는 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Courses(Arrays.asList(course, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스 목록에 null이 포함될 수 없습니다.");
        });
    }

    @Test
    @DisplayName("이동 코스 묶음은 외부에서 변경할 수 없다")
    void getCourses_ReturnsUnmodifiableList() {
        final List<Course> courseList = new ArrayList<>();
        courseList.add(course());

        final Courses courses = new Courses(courseList);

        courseList.clear();

        assertThat(courses.getCourses()).hasSize(1);
        assertThatThrownBy(() -> courses.getCourses().add(course()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private Course course() {
        return new Course(List.of(new Point(127.0, 37.0)));
    }

}
