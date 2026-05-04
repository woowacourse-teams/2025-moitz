package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.recommendation.RouteOriginDispersionService;
import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import com.f12.moitz.domain.route.OriginDestination;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.RouteOrigins;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouteOriginDispersionServiceTest {

    @Mock
    private RouteFinder routeFinder;

    @Test
    @DisplayName("출발지 간 경로의 이동시간으로 분산도 정책을 결정한다")
    void resolve() {
        final RouteOriginDispersionService service = new RouteOriginDispersionService(routeFinder);
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam));
        given(routeFinder.findRoutes(anyList())).willReturn(List.of(
                new Route(List.of(new Path(gangnam, yeoksam, TravelMethod.SUBWAY, 10 * 60, SubwayLine.fromTitle("2호선"))))
        ));

        final DispersionPolicy dispersionPolicy = service.resolve(routeOrigins);

        assertThat(dispersionPolicy).isEqualTo(DispersionPolicy.TIER_1);
        final ArgumentCaptor<List<OriginDestination>> captor = ArgumentCaptor.forClass(List.class);
        verify(routeFinder).findRoutes(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getOrigin()).isEqualTo(gangnam);
        assertThat(captor.getValue().get(0).getDestination()).isEqualTo(yeoksam);
    }

}
