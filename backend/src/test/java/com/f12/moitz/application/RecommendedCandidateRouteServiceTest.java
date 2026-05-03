package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.RouteFinder;
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
        final Map<Place, Routes> candidateRoutes = Map.of(
                seolleung, seolleungRoutes,
                samsung, samsungRoutes
        );
        given(routeFinder.findCourses(anyList())).willReturn(List.of(
                new Course(List.of(gangnam.getPoint(), seolleung.getPoint())),
                new Course(List.of(yeoksam.getPoint(), seolleung.getPoint())),
                new Course(List.of(gangnam.getPoint(), samsung.getPoint())),
                new Course(List.of(yeoksam.getPoint(), samsung.getPoint()))
        ));

        final RecommendedCandidateTravels result = service.prepare(
                routeOrigins,
                new RecommendedCandidates(
                        List.of(seolleung, samsung),
                        Map.of(
                                seolleung, List.of(CandidateSelectionTag.FAIRNESS),
                                samsung, List.of(CandidateSelectionTag.EFFICIENCY)
                        )
                ),
                candidateRoutes
        );

        assertThat(result.getRoutes(seolleung)).isEqualTo(seolleungRoutes);
        assertThat(result.getRoutes(samsung)).isEqualTo(samsungRoutes);
        assertThat(result.getCourses(seolleung).getCourses()).hasSize(2);
        assertThat(result.getCourses(samsung).getCourses()).hasSize(2);

        final ArgumentCaptor<List<OriginDestination>> captor = ArgumentCaptor.forClass(List.class);
        verify(routeFinder).findCourses(captor.capture());
        assertThat(captor.getValue()).hasSize(4);
        assertThat(captor.getValue())
                .extracting(OriginDestination::getDestination)
                .containsExactly(seolleung, seolleung, samsung, samsung);
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
