package com.f12.moitz.application;

import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Routes;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FinalPlaceTravelAssembly {

    private final Map<Place, Routes> routesByPlace;
    private final Map<Place, Courses> coursesByPlace;

    public FinalPlaceTravelAssembly(
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace
    ) {
        this.routesByPlace = Collections.unmodifiableMap(new LinkedHashMap<>(routesByPlace));
        this.coursesByPlace = Collections.unmodifiableMap(new LinkedHashMap<>(coursesByPlace));
    }

    public Map<Place, Routes> getRoutesByPlace() {
        return routesByPlace;
    }

    public Map<Place, Courses> getCoursesByPlace() {
        return coursesByPlace;
    }

}
