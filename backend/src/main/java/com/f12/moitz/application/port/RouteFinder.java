package com.f12.moitz.application.port;

import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.OriginDestination;
import java.util.List;

public interface RouteFinder {

    List<Route> findRoutes(List<OriginDestination> originDestinations);

    List<CandidateRoute> findCandidateRoutes(List<OriginDestination> originDestinations);

}
