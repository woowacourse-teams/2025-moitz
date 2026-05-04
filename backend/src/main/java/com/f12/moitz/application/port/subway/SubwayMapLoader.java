package com.f12.moitz.application.port.subway;

import com.f12.moitz.application.port.subway.dto.RawRouteInfo;
import java.util.List;

public interface SubwayMapLoader {

    List<RawRouteInfo> loadRawRoutes();

}
