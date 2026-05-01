package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteOrigins;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RouteOriginDispersionResolver {

    private final RouteFinder routeFinder;

    public RouteOriginDispersionResolver(
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder
    ) {
        this.routeFinder = routeFinder;
    }

    public DispersionPolicy resolve(final RouteOrigins routeOrigins) {
        validate(routeOrigins);

        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsBetweenOrigins();
        final List<Route> pairRoutes = routeFinder.findRoutes(originDestinations);
        final int pairMaxTravelTime = pairRoutes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .max()
                .orElse(0);
        final double pairAverageTravelTime = pairRoutes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .average()
                .orElse(0.0);
        final long longPairCount = pairRoutes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .filter(minutes -> minutes >= DispersionPolicy.LONG_PAIR_TRAVEL_TIME_MINUTES)
                .count();
        final DispersionPolicy dispersionPolicy = DispersionPolicy.resolve(
                routeOrigins.size(),
                pairMaxTravelTime,
                pairAverageTravelTime,
                longPairCount
        );

        log.debug(
                "출발지 분산도 판정 - 출발역={}, pairMax={}분, pairAvg={}분, longPairCount={}, policy={}",
                routeOrigins.getNames(),
                pairMaxTravelTime,
                String.format(Locale.US, "%.1f", pairAverageTravelTime),
                longPairCount,
                dispersionPolicy
        );
        return dispersionPolicy;
    }

    private void validate(final RouteOrigins routeOrigins) {
        if (routeOrigins == null) {
            throw new IllegalArgumentException("경로 출발지는 필수입니다.");
        }
    }

}
