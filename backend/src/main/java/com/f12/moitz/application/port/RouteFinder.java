package com.f12.moitz.application.port;

import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Route;
import java.util.List;

public interface RouteFinder {

    List<Route> findRoutes(List<StartEndPair> placePairs);

    List<Course> findCourses(List<StartEndPair> placePairs);

}
