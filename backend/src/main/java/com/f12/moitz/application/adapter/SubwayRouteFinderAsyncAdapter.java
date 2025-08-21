package com.f12.moitz.application.adapter;

import com.f12.moitz.application.PlaceService;
import com.f12.moitz.application.port.AsyncRouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.subway.SubwayMapPathFinder;
import com.f12.moitz.domain.subway.SubwayPath;
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
    private final PlaceService placeService;

    @Async("asyncTaskExecutor")
    @Override
    public CompletableFuture<List<Route>> findRoutesAsync(final List<StartEndPair> placePairs) {
        return CompletableFuture.completedFuture(findRoutes(placePairs));
    }

    @Override
    public List<Route> findRoutes(final List<StartEndPair> placePairs) {
        final List<Route> routes = Flux.fromIterable(placePairs)
                .flatMapSequential(pair -> {
                            final String startPlaceName = pair.start().getName();
                            final String endPlaceName = pair.end().getName();
                            return Mono.delay(Duration.ofMillis(50))
                                    .then(convertPath(
                                            subwayMapPathFinder.findShortestTimePath(startPlaceName, endPlaceName)
                                    ));
                        },
                        5)
                .map(Route::new)
                .collectList()
                .block();

        return routes;
    }

    private Mono<List<Path>> convertPath(final List<SubwayPath> subwayPaths) {
        return Mono.just(subwayPaths.stream()
                .map(subwayPath -> new Path(
                        placeService.findByName(subwayPath.fromName()),
                        placeService.findByName(subwayPath.toName()),
                        subwayPath.travelMethod(),
                        subwayPath.totalTime(),
                        subwayPath.lineName()
                ))
                .toList());
    }

}
