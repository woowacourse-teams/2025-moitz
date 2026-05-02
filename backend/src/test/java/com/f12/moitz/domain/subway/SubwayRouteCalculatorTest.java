package com.f12.moitz.domain.subway;

import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubwayRouteCalculatorTest {

    private SubwayRouteCalculator subwayRouteCalculator;
    private static final Point DEFAULT_POINT = new Point(127.0, 37.0);
    private final Set<SubwayEdge> subwayEdgeSet = new HashSet<>();

    private final SubwayStation station1 = new SubwayStation("청량리", DEFAULT_POINT);
    private final SubwayStation station2 = new SubwayStation("회기", DEFAULT_POINT);
    private final SubwayStation station3 = new SubwayStation("중랑", DEFAULT_POINT);
    private final SubwayStation station4 = new SubwayStation("상봉", DEFAULT_POINT);

    @BeforeEach
    void setUp() {
        SubwayEdge subwayEdge1 = new SubwayEdge(station1);
        subwayEdge1.addEdge(new Edge(station2, 180, 0, "1호선"));
        subwayEdge1.addEdge(new Edge(station2, 240, 0, "경의중앙선"));
        subwayEdge1.addEdge(new Edge(station2, 210, 0, "경춘선"));

        SubwayEdge subwayEdge2 = new SubwayEdge(station2);
        subwayEdge2.addEdge(new Edge(station1, 180, 0, "1호선"));
        subwayEdge2.addEdge(new Edge(station1, 240, 0, "경의중앙선"));
        subwayEdge2.addEdge(new Edge(station1, 210, 0, "경춘선"));
        subwayEdge2.addEdge(new Edge(station3, 180, 0, "경의중앙선"));
        subwayEdge2.addEdge(new Edge(station3, 150, 0, "경춘선"));
        subwayEdge2.addEdge(new Edge(station2, 1560, 0, "1호선"));
        subwayEdge2.addEdge(new Edge(station2, 1380, 0, "경의중앙선"));
        subwayEdge2.addEdge(new Edge(station2, 1560, 0, "경춘선"));

        SubwayEdge subwayEdge3 = new SubwayEdge(station3);
        subwayEdge3.addEdge(new Edge(station2, 180, 0, "경의중앙선"));
        subwayEdge3.addEdge(new Edge(station2, 150, 0, "경춘선"));
        subwayEdge3.addEdge(new Edge(station4, 120, 0, "경의중앙선"));
        subwayEdge3.addEdge(new Edge(station4, 150, 0, "경춘선"));

        SubwayEdge subwayEdge4 = new SubwayEdge(station4);
        subwayEdge4.addEdge(new Edge(station3, 120, 0, "경의중앙선"));
        subwayEdge4.addEdge(new Edge(station3, 150, 0, "경춘선"));

        subwayEdgeSet.add(subwayEdge1);
        subwayEdgeSet.add(subwayEdge2);
        subwayEdgeSet.add(subwayEdge3);
        subwayEdgeSet.add(subwayEdge4);

        subwayRouteCalculator = new SubwayRouteCalculator(new SubwayEdges(subwayEdgeSet));
    }

    @DisplayName("청량리-회기 최단경로를 찾는다.")
    @Test
    void findShortest() {
        // When
        final List<Path> paths = subwayRouteCalculator.findShortestTimePath(station1, station2).groupByLine();

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().getTravelTime().getSeconds()).isEqualTo(180);
            softly.assertThat(paths.getFirst().getSubwayLine()).isEqualTo(SubwayLine.fromTitle("1호선"));
        });
    }

}
