package com.f12.moitz.application.port;

import com.f12.moitz.domain.CandidateRoute;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.OriginDestination;
import java.util.List;

public interface RouteFinder {

    List<Route> findRoutes(List<OriginDestination> originDestinations);

    List<CandidateRoute> findCandidateRoutes(List<OriginDestination> originDestinations);

}
