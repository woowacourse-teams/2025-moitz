package com.f12.moitz.subway;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.adapter.SubwayRouteFinderAdapter;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Route;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Disabled
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SubwayRouteFinderAdapterTest {

    @Autowired
    private SubwayRouteFinderAdapter routeFinder;

    @DisplayName("노선도 만들고 최단경로 찾는다.")
    @Test
    void routeFindTest() {
        // Given
        final List<Place> startingPlaces = new ArrayList<>();
        startingPlaces.add(new Place("별내", new Point(125, 34)));
        startingPlaces.add(new Place("영등포구청", new Point(125, 34)));
        startingPlaces.add(new Place("모란", new Point(125, 34)));

        final List<Place> generatedPlaces = new ArrayList<>();
        generatedPlaces.add(new Place("신당", new Point(125, 34)));
        generatedPlaces.add(new Place("서울대입구", new Point(125, 34)));
        generatedPlaces.add(new Place("잠실", new Point(125, 34)));
        generatedPlaces.add(new Place("석촌", new Point(125, 34)));
        generatedPlaces.add(new Place("합정", new Point(125, 34)));

        final List<StartEndPair> allPairs = generatedPlaces.stream()
                .flatMap(endPlace -> startingPlaces.stream()
                        .map(startPlace -> new StartEndPair(startPlace, endPlace)))
                .toList();

        // When
        List<Route> routes = routeFinder.findRoutes(allPairs);

        // Then
        assertThat(routes).hasSize(15);
    }

}
