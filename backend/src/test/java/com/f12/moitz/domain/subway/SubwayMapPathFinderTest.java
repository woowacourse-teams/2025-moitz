package com.f12.moitz.domain.subway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubwayMapPathFinderTest {

    private SubwayMapPathFinder pathFinder;

    @BeforeEach
    void setUp() {
        Map<String, SubwayStation> map = new HashMap<>();

        SubwayStation station1 = new SubwayStation("청량리");
        map.put("청량리", station1);
        station1.addEdge(new Edge("회기", 180, 0, "1호선"));
        station1.addEdge(new Edge("회기", 240, 0, "경의선"));
        station1.addEdge(new Edge("회기", 210, 0, "경춘선"));

        SubwayStation station2 = new SubwayStation("회기");
        map.put("회기", station2);
        station2.addEdge(new Edge("청량리", 180, 0, "1호선"));
        station2.addEdge(new Edge("청량리", 240, 0, "경의선"));
        station2.addEdge(new Edge("청량리", 210, 0, "경춘선"));
        station2.addEdge(new Edge("중랑", 180, 0, "경의선"));
        station2.addEdge(new Edge("중랑", 150, 0, "경춘선"));
        station2.addEdge(new Edge("회기", 1560, 0, "1호선"));
        station2.addEdge(new Edge("회기", 1380, 0, "경의선"));
        station2.addEdge(new Edge("회기", 1560, 0, "경춘선"));

        SubwayStation station3 = new SubwayStation("중랑");
        map.put("중랑", station3);
        station3.addEdge(new Edge("회기", 180, 0, "경의선"));
        station3.addEdge(new Edge("회기", 150, 0, "경춘선"));
        station3.addEdge(new Edge("상봉", 120, 0, "경의선"));
        station3.addEdge(new Edge("상봉", 150, 0, "경춘선"));

        SubwayStation station4 = new SubwayStation("상봉");
        map.put("상봉", station4);
        station4.addEdge(new Edge("중랑", 120, 0, "경의선"));
        station4.addEdge(new Edge("중랑", 150, 0, "경춘선"));

        pathFinder = new SubwayMapPathFinder(map);
    }

    @DisplayName("청량리-회기 최단경로를 찾는다.")
    @Test
    void findShortest() {
        // Given

        // When
        List<SubwayPath> paths = pathFinder.findShortestTimePath("청량리", "회기");

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().totalTime()).isEqualTo(180);
            softly.assertThat(paths.getFirst().lineName()).isEqualTo("1호선");
        });
    }

}
