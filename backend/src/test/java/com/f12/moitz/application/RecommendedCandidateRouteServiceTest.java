package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.CandidateRoute;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendedCandidateRouteServiceTest {

    @Mock
    private RouteFinder routeFinder;

    @Test
    @DisplayName("선택된 추천 후보의 경로와 코스를 장소별로 조립한다")
    void assemble() {
        final RecommendedCandidateRouteService service = new RecommendedCandidateRouteService(routeFinder);
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final Place samsung = new Place("삼성역", new Point(127.063, 37.508));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam));
        final Routes seolleungRoutes = createRoutes(List.of(gangnam, yeoksam), seolleung);
        final Routes samsungRoutes = createRoutes(List.of(gangnam, yeoksam), samsung);
        given(routeFinder.findCandidateRoutes(anyList())).willReturn(List.of(
                new CandidateRoute(seolleungRoutes.get(0), new Course(List.of(gangnam.getPoint(), seolleung.getPoint()))),
                new CandidateRoute(seolleungRoutes.get(1), new Course(List.of(yeoksam.getPoint(), seolleung.getPoint()))),
                new CandidateRoute(samsungRoutes.get(0), new Course(List.of(gangnam.getPoint(), samsung.getPoint()))),
                new CandidateRoute(samsungRoutes.get(1), new Course(List.of(yeoksam.getPoint(), samsung.getPoint())))
        ));

        final RecommendedCandidateTravels result = service.prepare(
                routeOrigins,
                new RecommendedCandidates(
                        List.of(seolleung, samsung),
                        Map.of(
                                seolleung, List.of(CandidateSelectionTag.FAIRNESS),
                                samsung, List.of(CandidateSelectionTag.EFFICIENCY)
                        )
                )
        );

        assertThat(result.getCandidateRoutes(seolleung))
                .extracting(candidateRoute -> candidateRoute.getRoute())
                .containsExactly(seolleungRoutes.get(0), seolleungRoutes.get(1));
        assertThat(result.getCandidateRoutes(samsung))
                .extracting(candidateRoute -> candidateRoute.getRoute())
                .containsExactly(samsungRoutes.get(0), samsungRoutes.get(1));
        assertThat(result.getCandidateRoutes(seolleung))
                .extracting(candidateRoute -> candidateRoute.getCourse())
                .hasSize(2);
        assertThat(result.getCandidateRoutes(samsung))
                .extracting(candidateRoute -> candidateRoute.getCourse())
                .hasSize(2);

        final ArgumentCaptor<List<OriginDestination>> captor = ArgumentCaptor.forClass(List.class);
        verify(routeFinder).findCandidateRoutes(captor.capture());
        assertThat(captor.getValue()).hasSize(4);
        assertThat(captor.getValue())
                .extracting(OriginDestination::getDestination)
                .containsExactly(seolleung, seolleung, samsung, samsung);
    }

    @Test
    @DisplayName("후보 경로 조회 결과 개수가 요청 개수와 다르면 추천 후보 이동 정보를 조립할 수 없다")
    void prepare_ThrowsExceptionWhenCandidateRouteCountDoesNotMatchOriginDestinations() {
        final RecommendedCandidateRouteService service = new RecommendedCandidateRouteService(routeFinder);
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam));
        final Routes seolleungRoutes = createRoutes(List.of(gangnam, yeoksam), seolleung);
        given(routeFinder.findCandidateRoutes(anyList())).willReturn(List.of(
                new CandidateRoute(seolleungRoutes.get(0), new Course(List.of(gangnam.getPoint(), seolleung.getPoint())))
        ));

        assertThatThrownBy(() -> service.prepare(
                routeOrigins,
                new RecommendedCandidates(
                        List.of(seolleung),
                        Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
                )
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("추천 후보 경로 조회 결과 개수가 일치하지 않습니다. 요청=2, 응답=1");
    }

    @Test
    @DisplayName("후보 경로 조회 결과가 null이면 추천 후보 이동 정보를 조립할 수 없다")
    void prepare_ThrowsExceptionWhenCandidateRoutesAreNull() {
        final RecommendedCandidateRouteService service = new RecommendedCandidateRouteService(routeFinder);
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam));
        given(routeFinder.findCandidateRoutes(anyList())).willReturn(null);

        assertThatThrownBy(() -> service.prepare(
                routeOrigins,
                new RecommendedCandidates(
                        List.of(seolleung),
                        Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
                )
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("추천 후보 경로 조회 결과가 null입니다.");
    }

    private Routes createRoutes(final List<Place> origins, final Place destination) {
        return new Routes(origins.stream()
                .map(origin -> new Route(List.of(new Path(
                        origin,
                        destination,
                        TravelMethod.SUBWAY,
                        10 * 60,
                        SubwayLine.fromTitle("2호선")
                ))))
                .toList());
    }

}
