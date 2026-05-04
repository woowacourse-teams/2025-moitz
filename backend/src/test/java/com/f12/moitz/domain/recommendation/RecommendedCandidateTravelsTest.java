package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.Course;
import com.f12.moitz.domain.route.Courses;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedCandidateTravelsTest {

    @Test
    @DisplayName("추천 후보 경로와 이동 코스의 장소 목록이 다르면 생성할 수 없다")
    void throwsExceptionWhenPlacesAreDifferent() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");

        assertThatThrownBy(() -> new RecommendedCandidateTravels(
                Map.of(seolleung, routes(seolleung)),
                Map.of(samsung, courses(samsung))
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 후보 경로와 이동 코스의 장소 목록은 같아야 합니다.");
    }

    @Test
    @DisplayName("추천 후보 경로와 이동 코스의 개수가 다르면 생성할 수 없다")
    void throwsExceptionWhenRouteAndCourseSizesAreDifferent() {
        final Place seolleung = place("선릉역");

        assertThatThrownBy(() -> new RecommendedCandidateTravels(
                Map.of(seolleung, routes(seolleung, 2)),
                Map.of(seolleung, courses(seolleung, 1))
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 후보 경로와 이동 코스의 개수는 같아야 합니다. 추천 지역: 선릉역");
    }

    @Test
    @DisplayName("추천 후보 경로와 이동 코스는 null일 수 없다")
    void throwsExceptionWhenArgumentsAreNull() {
        final Place seolleung = place("선릉역");

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidateTravels(null, Map.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 경로는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidateTravels(Map.of(), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 이동 코스는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidateTravels(
                            mapOf(seolleung, null),
                            Map.of(seolleung, courses(seolleung))
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 경로는 null일 수 없습니다. 추천 지역: 선릉역");
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidateTravels(
                            Map.of(seolleung, routes(seolleung)),
                            mapOf(seolleung, null)
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 이동 코스는 null일 수 없습니다. 추천 지역: 선릉역");
        });
    }

    @Test
    @DisplayName("추천 후보 경로와 이동 코스를 하나의 후보 경로 단위로 제공한다")
    void getCandidateRoutes() {
        final Place seolleung = place("선릉역");
        final Routes routes = routes(seolleung, 2);
        final Courses courses = courses(seolleung, 2);
        final RecommendedCandidateTravels travels = new RecommendedCandidateTravels(
                Map.of(seolleung, routes),
                Map.of(seolleung, courses)
        );

        final List<CandidateRoute> candidateRoutes = travels.getCandidateRoutes(seolleung);

        assertThat(candidateRoutes).hasSize(2);
        assertThat(candidateRoutes.get(0).getRoute()).isEqualTo(routes.get(0));
        assertThat(candidateRoutes.get(0).getCourse()).isEqualTo(courses.get(0));
        assertThat(candidateRoutes.get(1).getRoute()).isEqualTo(routes.get(1));
        assertThat(candidateRoutes.get(1).getCourse()).isEqualTo(courses.get(1));
    }

    @Test
    @DisplayName("후보 경로 목록으로 추천 후보 이동 정보를 생성한다")
    void constructor_FromCandidateRoutes() {
        final Place seolleung = place("선릉역");
        final Routes routes = routes(seolleung, 2);
        final Courses courses = courses(seolleung, 2);

        final RecommendedCandidateTravels travels = new RecommendedCandidateTravels(Map.of(
                seolleung,
                List.of(
                        new CandidateRoute(routes.get(0), courses.get(0)),
                        new CandidateRoute(routes.get(1), courses.get(1))
                )
        ));

        final List<CandidateRoute> candidateRoutes = travels.getCandidateRoutes(seolleung);
        assertThat(candidateRoutes).hasSize(2);
        assertThat(candidateRoutes.get(0).getRoute()).isEqualTo(routes.get(0));
        assertThat(candidateRoutes.get(0).getCourse()).isEqualTo(courses.get(0));
        assertThat(candidateRoutes.get(1).getRoute()).isEqualTo(routes.get(1));
        assertThat(candidateRoutes.get(1).getCourse()).isEqualTo(courses.get(1));
    }

    @Test
    @DisplayName("추천 후보가 아닌 장소의 후보 경로는 조회할 수 없다")
    void getCandidateRoutes_ThrowsExceptionWhenPlaceIsNotRecommendedCandidate() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidateTravels travels = new RecommendedCandidateTravels(
                Map.of(seolleung, routes(seolleung)),
                Map.of(seolleung, courses(seolleung))
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> travels.getCandidateRoutes(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 장소는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> travels.getCandidateRoutes(samsung))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 경로가 누락되었습니다. 추천 지역: 삼성역");
        });
    }

    private <T> Map<Place, T> mapOf(final Place place, final T value) {
        final Map<Place, T> map = new LinkedHashMap<>();
        map.put(place, value);
        return map;
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

    private Routes routes(final Place destination) {
        return routes(destination, 1);
    }

    private Routes routes(final Place destination, final int count) {
        final Place origin = place("출발역");
        return new Routes(java.util.stream.IntStream.range(0, count)
                .mapToObj(index -> new Route(List.of(new Path(
                        origin,
                        destination,
                        TravelMethod.SUBWAY,
                        10 * 60,
                        SubwayLine.fromTitle("2호선")
                ))))
                .toList());
    }

    private Courses courses(final Place destination) {
        return courses(destination, 1);
    }

    private Courses courses(final Place destination, final int count) {
        final Place origin = place("출발역");
        return new Courses(java.util.stream.IntStream.range(0, count)
                .mapToObj(index -> new Course(List.of(origin.getPoint(), destination.getPoint())))
                .toList());
    }

}
