package com.f12.moitz.subway;

import com.f12.moitz.application.port.AsyncRouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Route;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubwayRouteFinderAsyncAdapter implements AsyncRouteFinder {

    private final SubwayMapPathFinder subwayMapPathFinder;

    @Async("asyncTaskExecutor")
    @Override
    public CompletableFuture<List<Route>> findRoutesAsync(final List<StartEndPair> placePairs) {
        return CompletableFuture.completedFuture(findRoutes(placePairs));
    }

    @Override
    public List<Route> findRoutes(final List<StartEndPair> placePairs) {
        List<Route> routes = Flux.fromIterable(placePairs)
                .flatMapSequential(pair -> {
                            final String startPlaceName = getStationName(pair.start().getName());
                            final String endPlaceName = getStationName(pair.end().getName());
                            return Mono.delay(Duration.ofMillis(50))
                                    .then(subwayMapPathFinder.findShortestTimePathMono(startPlaceName, endPlaceName));
                        },
                        5)
                .map(Route::new)
                .collectList()
                .block();

        return routes;
    }

    private String getStationName(final String stationName) {
        if (!stationName.equals("서울역") && stationName.endsWith("역")) {
            return stationName.substring(0, stationName.length() - 1);
        }
        return stationName;
    }

}
