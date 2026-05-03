package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteOrigins;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RouteOriginDispersionService {

    private final RouteFinder routeFinder;

    public RouteOriginDispersionService(
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder
    ) {
        this.routeFinder = routeFinder;
    }

    public DispersionPolicy resolve(final RouteOrigins routeOrigins) {
        validate(routeOrigins);

        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsBetweenOrigins();
        final List<Route> pairRoutes = routeFinder.findRoutes(originDestinations);
        final DispersionPolicy dispersionPolicy = routeOrigins.resolveDispersionPolicy(pairRoutes);

        log.debug(
                "출발지 분산도 판정 - 출발역={}, pairRoutes={}개, policy={}",
                routeOrigins.getNames(),
                pairRoutes.size(),
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
