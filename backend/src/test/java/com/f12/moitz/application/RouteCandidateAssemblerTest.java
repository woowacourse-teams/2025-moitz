package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouteCandidateAssemblerTest {

    @Mock
    private SubwayStationService subwayStationService;

    @Mock
    private RouteFinder routeFinder;

    @Test
    @DisplayName("출발지를 제외한 후보 지역과 후보별 경로를 조립한다")
    void assemble() {
        final RouteCandidateAssembler assembler = new RouteCandidateAssembler(subwayStationService, routeFinder);
        final SubwayStation gangnam = new SubwayStation("강남역", new Point(127.027, 37.497));
        final SubwayStation yeoksam = new SubwayStation("역삼역", new Point(127.036, 37.501));
        final SubwayStation seolleung = new SubwayStation("선릉역", new Point(127.048, 37.504));
        final SubwayStation samsung = new SubwayStation("삼성역", new Point(127.063, 37.508));
        final List<SubwayStation> originStations = List.of(gangnam, yeoksam);
        final RouteOrigins routeOrigins = new RouteOrigins(originStations);
        given(subwayStationService.generateCandidatePlace(originStations, 10))
                .willReturn(List.of(gangnam, yeoksam, seolleung, samsung));
        given(routeFinder.findRoutes(anyList())).willReturn(List.of(
                createRoute(gangnam, seolleung, 10),
                createRoute(yeoksam, seolleung, 5),
                createRoute(gangnam, samsung, 14),
                createRoute(yeoksam, samsung, 10)
        ));

        final RouteCandidateAssembly assembly = assembler.assemble(
                originStations,
                routeOrigins,
                DispersionPolicy.TIER_1
        );

        assertThat(assembly.getCandidatePlaces()).containsExactly(seolleung, samsung);
        assertThat(assembly.getCandidateRoutes()).containsOnlyKeys(seolleung, samsung);
        assertThat(assembly.getRouteCandidates())
                .extracting(RouteCandidate::getPlace)
                .containsExactly(seolleung, samsung);
        assertThat(assembly.getRouteCandidates())
                .allSatisfy(candidate -> assertThat(candidate.getRoutes().getRoutes()).hasSize(2));

        final ArgumentCaptor<List<OriginDestination>> captor = ArgumentCaptor.forClass(List.class);
        verify(routeFinder).findRoutes(captor.capture());
        assertThat(captor.getValue()).hasSize(4);
        assertThat(captor.getValue())
                .extracting(OriginDestination::getDestination)
                .containsExactly(seolleung, seolleung, samsung, samsung);
    }

    private Route createRoute(final Place origin, final Place destination, final int minutes) {
        return new Route(List.of(new Path(
                origin,
                destination,
                TravelMethod.SUBWAY,
                minutes * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

}
