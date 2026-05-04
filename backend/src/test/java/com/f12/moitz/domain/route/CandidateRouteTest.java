package com.f12.moitz.domain.route;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidateRouteTest {

    @Test
    @DisplayName("후보 경로 단위는 경로와 이동 코스가 필수다")
    void constructor_ThrowsExceptionWhenArgumentsAreInvalid() {
        final Place start = new Place("출발", new Point(127.0, 37.0));
        final Place end = new Place("도착", new Point(127.1, 37.1));
        final Route route = new Route(List.of(Path.subway(start, end, 10, SubwayLine.fromTitle("2호선"))));
        final Course course = new Course(List.of(start.getPoint(), end.getPoint()));

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new CandidateRoute(null, course))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("후보 경로는 필수입니다.");
            softAssertions.assertThatThrownBy(() -> new CandidateRoute(route, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("후보 이동 코스는 필수입니다.");
        });
    }

}
