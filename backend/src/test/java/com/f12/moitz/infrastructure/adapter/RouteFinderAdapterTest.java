package com.f12.moitz.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.infrastructure.client.odsay.OdsayClient;
import com.f12.moitz.infrastructure.client.odsay.dto.InfoResponse;
import com.f12.moitz.infrastructure.client.odsay.dto.LaneResponse;
import com.f12.moitz.infrastructure.client.odsay.dto.PathResponse;
import com.f12.moitz.infrastructure.client.odsay.dto.ResultResponse;
import com.f12.moitz.infrastructure.client.odsay.dto.SubPathResponse;
import com.f12.moitz.infrastructure.client.odsay.dto.SubwayRouteSearchResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouteFinderAdapterTest {

    @InjectMocks
    private RouteFinderAdapter routeFinderAdapter;

    @Mock
    private OdsayClient odsayClient;

    @Test
    @DisplayName("ODsay API 응답을 받아 경로를 도메인 Route 객체로 올바르게 변환한다")
    void findRouteTest() {
        // Given
        final Place start = new Place("강남역", new Point(127.027, 37.497));
        final Place end = new Place("광화문역", new Point(126.977, 37.575));
        final List<StartEndPair> pairs = List.of(new StartEndPair(start, end));

        final var walkPathStart = new SubPathResponse(3, 5, 0, "출발지", 0, 0, "강남역", 0, 0, Collections.emptyList(), null, null, null, null, null, null, null);
        final var subwayPath1 = new SubPathResponse(1, 15, 0, "강남역", 127.02, 37.49, "시청역", 126.97, 37.56, List.of(new LaneResponse("2호선", 2)), null, null, null, null, null, null, null);
        final var transferPath = new SubPathResponse(3, 3, 0, null, 0, 0, null, 0, 0, Collections.emptyList(), null, null, null, null, null, null, null);
        final var subwayPath2 = new SubPathResponse(1, 5, 0, "시청역", 126.97, 37.56, "광화문역", 126.97, 37.57, List.of(new LaneResponse("5호선", 5)), null, null, null, null, null, null, null);
        final var walkPathEnd = new SubPathResponse(3, 5, 0, "광화문역", 0, 0, "도착지", 0, 0, Collections.emptyList(), null, null, null, null, null, null, null);

        final var subPathListShort = new ArrayList<>(List.of(walkPathStart, subwayPath1, transferPath, subwayPath2, walkPathEnd));
        final var subPathListLong = new ArrayList<>(List.of(walkPathStart, subwayPath1, walkPathEnd));

        final var pathResponseShort = new PathResponse(1, new InfoResponse(0, 0, 28, 0, 1, "", "", "", 0, null), subPathListShort);
        final var pathResponseLong = new PathResponse(1, new InfoResponse(0, 0, 40, 0, 0, "", "", "", 0, null), subPathListLong);

        final var resultResponse = new ResultResponse(2, List.of(pathResponseLong, pathResponseShort));
        final var mockResponse = new SubwayRouteSearchResponse(resultResponse, null);

        given(odsayClient.getRoute(any(Point.class), any(Point.class))).willReturn(mockResponse);

        // When
        final List<Route> actualRoute = routeFinderAdapter.findRoutes(pairs);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actualRoute).isNotNull();
            final List<Path> actualPaths = actualRoute.getFirst().getPaths();

            softAssertions.assertThat(actualPaths).hasSize(3);

            final Path firstLeg = actualPaths.getFirst();
            softAssertions.assertThat(firstLeg.getStart().getName()).isEqualTo("강남역");
            softAssertions.assertThat(firstLeg.getEnd().getName()).isEqualTo("시청역");
            softAssertions.assertThat(firstLeg.getTravelMethod()).isEqualTo(TravelMethod.SUBWAY);
            softAssertions.assertThat(firstLeg.getTravelTime().toMinutes()).isEqualTo(15);
            softAssertions.assertThat(firstLeg.getSubwayLineName()).isEqualTo("2호선");

            final Path transferLeg = actualPaths.get(1);
            softAssertions.assertThat(transferLeg.getTravelMethod()).isEqualTo(TravelMethod.TRANSFER);
            softAssertions.assertThat(transferLeg.getStart().getName()).isEqualTo(firstLeg.getEnd().getName());
            softAssertions.assertThat(transferLeg.getTravelTime().toMinutes()).isEqualTo(3);

            final Path thirdLeg = actualPaths.getLast();
            softAssertions.assertThat(thirdLeg.getStart().getName()).isEqualTo("시청역");
            softAssertions.assertThat(thirdLeg.getEnd().getName()).isEqualTo("광화문역");
            softAssertions.assertThat(thirdLeg.getTravelMethod()).isEqualTo(TravelMethod.SUBWAY);
            softAssertions.assertThat(thirdLeg.getTravelTime().toMinutes()).isEqualTo(5);
            softAssertions.assertThat(thirdLeg.getSubwayLineName()).isEqualTo("5호선");
        });
    }

    @Test
    @DisplayName("ODsay API 응답에서 경로 정보를 찾을 수 없을 때 예외를 던진다")
    void throwsExceptionNoPathFound() {
        // Given
        final Place startPlace = new Place("출발지", new Point(127.0, 37.0));
        final Place endPlace = new Place("도착지", new Point(127.1, 37.1));
        final List<StartEndPair> pairs = List.of(new StartEndPair(startPlace, endPlace));

        final var resultResponse = new ResultResponse(0, Collections.emptyList());
        final var mockResponseWithEmptyPath = new SubwayRouteSearchResponse(resultResponse, null);
        given(odsayClient.getRoute(any(Point.class), any(Point.class))).willReturn(mockResponseWithEmptyPath);

        // When & Then
        assertThatThrownBy(() -> routeFinderAdapter.findRoutes(pairs))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("경로 정보를 찾을 수 없습니다.");
    }
}
