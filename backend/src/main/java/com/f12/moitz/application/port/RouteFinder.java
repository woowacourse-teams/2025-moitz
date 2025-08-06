package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Route;

public interface RouteFinder {

    Route findRoute(Place startPlace, Place endPlace);

}
