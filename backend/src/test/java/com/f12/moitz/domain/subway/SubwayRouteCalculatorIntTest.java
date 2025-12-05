package com.f12.moitz.domain.subway;

import com.f12.moitz.application.SubwayStationService;
import com.f12.moitz.domain.Point;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Disabled
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SubwayRouteCalculatorIntTest {

    private final SubwayStationService subwayStationService;
    private final SubwayRouteCalculator subwayRouteCalculator;

    public SubwayRouteCalculatorIntTest(
            @Autowired final SubwayStationService subwayStationService,
            @Autowired final SubwayRouteCalculator subwayRouteCalculator
    ) {
        this.subwayStationService = subwayStationService;
        this.subwayRouteCalculator = subwayRouteCalculator;
    }

    @DisplayName("미금역-판교역 최단경로를 찾는다.")
    @Test
    void findShortest() {
        // Given
        final SubwayStation start = subwayStationService.getByName("미금역");
        final SubwayStation end = subwayStationService.getByName("판교역");

        // When
        final List<SubwayPath> paths = subwayRouteCalculator.findShortestTimePath(start, end).groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
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
        // Given
        final SubwayStation start = subwayStationService.getByName("미금역");
        final SubwayStation end = subwayStationService.getByName("정자역");

        // When
        final List<SubwayPath> paths = subwayRouteCalculator.findShortestTimePath(start, end).groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().totalTime()).isEqualTo(150);
            softly.assertThat(List.of(SubwayLine.fromTitle("신분당선"), SubwayLine.fromTitle("수인분당선")))
                    .contains(paths.getFirst().line());
        });
    }

    @DisplayName("오이도역-사리역 최단경로를 찾는다.")
    @Test
    void findShortest3() {
        // Given
        final SubwayStation start = subwayStationService.getByName("오이도역");
        final SubwayStation end = subwayStationService.getByName("사리역");

        // When
        final List<SubwayPath> paths = subwayRouteCalculator.findShortestTimePath(start, end).groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
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
        // Given
        final SubwayStation start = subwayStationService.getByName("오이도역");
        final SubwayStation end = subwayStationService.getByName("상록수역");

        // When
        final List<SubwayPath> paths = subwayRouteCalculator.findShortestTimePath(start, end).groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("4호선"));
        });
    }

    @DisplayName("서울역-광화문역 최단경로를 찾는다.")
    @Test
    void findShortest5() {
        // Given
        final SubwayStation start = subwayStationService.getByName("서울역");
        final SubwayStation end = subwayStationService.getByName("광화문역");

        // When
        final List<SubwayPath> paths = subwayRouteCalculator.findShortestTimePath(start, end).groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(3);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("공항철도"));
        });
    }

    @DisplayName("잠실나루역-올림픽공원역 최단경로를 찾는다.")
    @Test
    void findShortest6() {
        // Given
        final SubwayStation start = subwayStationService.getByName("잠실나루역");
        final SubwayStation end = subwayStationService.getByName("올림픽공원역");

        // When
        final List<SubwayPath> paths = subwayRouteCalculator.findShortestTimePath(start, end).groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(5);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("2호선"));
        });
    }

    @DisplayName("잠실역-잠실나루역 최단경로를 찾는다.")
    @Test
    void findShortest7() {
        // Given
        final SubwayStation start = subwayStationService.getByName("잠실역");
        final SubwayStation end = subwayStationService.getByName("잠실나루역");

        // When
        final StationSequence stationSequence = subwayRouteCalculator.findShortestTimePath(start, end);
        log.debug("");
        final List<SubwayPath> paths = stationSequence.groupByLine();
        for (SubwayPath path : paths) {
            log.debug("출발: {}, 도착: {}, 호선: {}", path.from().getName(), path.to().getName(),
                    path.line() == null ? "null" : path.line().getTitle());
        }

        // Then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(paths).hasSize(1);
            softly.assertThat(paths.getFirst().line()).isEqualTo(SubwayLine.fromTitle("2호선"));
        });
    }

    @DisplayName("목데이터 생성용 최단 경로의 경유역 찾기")
    @ParameterizedTest
    @CsvSource({"강변역,종각역", "동대문역,종각역", "서울대입구역,종각역",
            "강변역,건대입구역", "동대문역,건대입구역", "서울대입구역,건대입구역",
            "강변역,사당역", "동대문역,사당역", "서울대입구역,사당역",
            "강변역,왕십리역", "동대문역,왕십리역", "서울대입구역,왕십리역",
            "강변역,홍대입구역", "동대문역,홍대입구역", "서울대입구역,홍대입구역"})
    void createMockData(final String start, final String end) {
        final StationSequence stationSequence = subwayRouteCalculator.findShortestTimePath(
                subwayStationService.getByName(start),
                subwayStationService.getByName(end)
        );

        final List<Point> points = stationSequence.getPoints();

        for (Point point : points) {
            log.info("{ \"x\": {}, \"y\": {} },", point.getX(), point.getY());
        }
    }

}
