package com.f12.moitz.application.recommendation;

import com.f12.moitz.application.subway.SubwayRouteService;
import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import com.f12.moitz.domain.route.OriginDestinations;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.RouteOrigins;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouteOriginDispersionService {

    private final SubwayRouteService subwayRouteService;

    public RouteOriginDispersionService(final SubwayRouteService subwayRouteService) {
        this.subwayRouteService = subwayRouteService;
    }

    public DispersionPolicy resolve(final RouteOrigins routeOrigins) {
        validate(routeOrigins);

        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsBetweenOrigins();
        final List<Route> pairRoutes = subwayRouteService.findRoutes(originDestinations.getValues());
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
