package com.f12.moitz.application.port;

import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.OriginDestination;
import java.util.List;

public interface RouteFinder {

    List<Route> findRoutes(List<OriginDestination> originDestinations);

    List<Course> findCourses(List<OriginDestination> originDestinations);

}
