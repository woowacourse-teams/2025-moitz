package com.f12.moitz.application.port;

import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Route;
import java.util.List;

public interface RouteFinder {

    List<Route> findRoutes(final List<StartEndPair> placePairs);

}
