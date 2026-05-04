package com.f12.moitz.application.subway;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.port.subway.dto.RawPathInfo;
import com.f12.moitz.application.port.subway.dto.RawRouteInfo;
import com.f12.moitz.application.port.subway.dto.RawStationInfo;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubwayEdgesBuilderTest {

    private static final Point POINT = new Point(127.0, 37.0);

    private final SubwayEdgesBuilder subwayEdgesBuilder = new SubwayEdgesBuilder();

    @Test
    @DisplayName("일반 구간은 양방향 엣지를 만들고 다음 출발 시각과의 차이로 이동 시간을 계산한다")
    void build_CreatesBidirectionalEdges() {
        final SubwayStation gangnam = new SubwayStation("강남역", POINT);
        final SubwayStation yeoksam = new SubwayStation("역삼역", POINT);
        final SubwayStation seolleung = new SubwayStation("선릉역", POINT);
        final RawRouteInfo rawRoute = new RawRouteInfo(List.of(
                new RawPathInfo(
                        new RawStationInfo("강남역", "2호선"),
                        new RawStationInfo("역삼역", "2호선"),
                        800,
                        false,
                        0,
                        30,
                        "10:00:00"
                ),
                new RawPathInfo(
                        new RawStationInfo("역삼역", "2호선"),
                        new RawStationInfo("선릉역", "2호선"),
                        700,
                        false,
                        0,
                        40,
                        "10:02:00"
                )
        ));

        final SubwayEdges subwayEdges = subwayEdgesBuilder.build(List.of(gangnam, yeoksam, seolleung), List.of(rawRoute));

        assertThat(subwayEdges.getEdges(gangnam))
                .anySatisfy(edge -> assertEdge(edge, yeoksam, 120, 800, SubwayLine.SEOUL_METRO_LINE2));
        assertThat(subwayEdges.getEdges(yeoksam))
                .anySatisfy(edge -> assertEdge(edge, gangnam, 120, 800, SubwayLine.SEOUL_METRO_LINE2))
                .anySatisfy(edge -> assertEdge(edge, seolleung, 40, 700, SubwayLine.SEOUL_METRO_LINE2));
        assertThat(subwayEdges.getEdges(seolleung))
                .anySatisfy(edge -> assertEdge(edge, yeoksam, 40, 700, SubwayLine.SEOUL_METRO_LINE2));
    }

    @Test
    @DisplayName("환승 구간은 출발 노선과 도착 노선 엣지를 함께 만들고 역방향 엣지는 만들지 않는다")
    void build_CreatesTransferEdges() {
        final SubwayStation gangnam = new SubwayStation("강남역", POINT);
        final SubwayStation sinnonnyeon = new SubwayStation("신논현역", POINT);
        final RawRouteInfo rawRoute = new RawRouteInfo(List.of(new RawPathInfo(
                new RawStationInfo("강남역", "2호선"),
                new RawStationInfo("신논현역", "신분당선"),
                0,
                true,
                60,
                120,
                null
        )));

        final SubwayEdges subwayEdges = subwayEdgesBuilder.build(List.of(gangnam, sinnonnyeon), List.of(rawRoute));

        assertThat(subwayEdges.getEdges(gangnam))
                .hasSize(2)
                .anySatisfy(edge -> assertEdge(edge, sinnonnyeon, 180, 0, SubwayLine.SEOUL_METRO_LINE2))
                .anySatisfy(edge -> assertEdge(edge, sinnonnyeon, 180, 0, SubwayLine.SIN_BUNDANG));
        assertThat(subwayEdges.getEdges(sinnonnyeon)).isEmpty();
    }

    private void assertEdge(
            final Edge edge,
            final SubwayStation destination,
            final int timeInSeconds,
            final int distance,
            final SubwayLine subwayLine
    ) {
        assertThat(edge.getDestination()).isEqualTo(destination);
        assertThat(edge.getTimeInSeconds()).isEqualTo(timeInSeconds);
        assertThat(edge.getDistance()).isEqualTo(distance);
        assertThat(edge.getSubwayLine()).isEqualTo(subwayLine);
    }

}
