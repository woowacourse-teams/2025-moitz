package com.f12.moitz.domain.subway;

import com.f12.moitz.application.SubwayEdgeService;
import com.f12.moitz.application.SubwayStationService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Disabled
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SubwayEdgesIntTest {

    private final SubwayStationService subwayStationService;
    private final SubwayEdges subwayEdges;

    public SubwayEdgesIntTest(@Autowired final SubwayStationService subwayStationService, @Autowired final SubwayEdgeService subwayEdgeService) {
        this.subwayStationService = subwayStationService;
        this.subwayEdges = subwayEdgeService.getSubwayEdges();
    }

    @DisplayName("미금역-판교역 최단경로를 찾는다.")
    @Test
    void findShortest() {
        SubwayStation start = subwayStationService.getByName("미금역");
        SubwayStation end = subwayStationService.getByName("판교역");

        // When
        List<SubwayPath> paths = subwayEdges.findShortestTimePath(start, end);
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(), path.line() == null? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().totalTime()).isEqualTo(355);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("신분당선"));
        });
    }

    @DisplayName("미금역-정자역 최단경로를 찾는다.")
    @Test
    void findShortest2() {
        SubwayStation start = subwayStationService.getByName("미금역");
        SubwayStation end = subwayStationService.getByName("정자역");

        // When
        List<SubwayPath> paths = subwayEdges.findShortestTimePath(start, end);
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(), path.line() == null? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().totalTime()).isEqualTo(150);
            softly.assertThat(List.of(SubwayLine.fromTitle("신분당선"), SubwayLine.fromTitle("수인분당선"))).contains(paths.getFirst().line());
        });
    }

    @DisplayName("오이도역-사리역 최단경로를 찾는다.")
    @Test
    void findShortest3() {
        SubwayStation start = subwayStationService.getByName("오이도역");
        SubwayStation end = subwayStationService.getByName("사리역");

        // When
        List<SubwayPath> paths = subwayEdges.findShortestTimePath(start, end);
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(), path.line() == null? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("수인분당선"));
        });
    }

    @DisplayName("오이도역-상록수역 최단경로를 찾는다.")
    @Test
    void findShortest4() {
        SubwayStation start = subwayStationService.getByName("오이도역");
        SubwayStation end = subwayStationService.getByName("상록수역");

        // When
        List<SubwayPath> paths = subwayEdges.findShortestTimePath(start, end);
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(), path.line() == null? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("4호선"));
        });
    }

}
