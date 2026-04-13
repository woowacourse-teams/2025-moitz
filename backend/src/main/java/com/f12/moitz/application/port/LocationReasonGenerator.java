package com.f12.moitz.application.port;

import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.domain.RecommendCondition;
import java.util.List;
import java.util.Map;

public interface LocationReasonGenerator {

    Map<String, ReasonAndDescription> generateReasons(
            List<String> startingPlaces,
            List<String> selectedPlaces,
            List<RecommendCondition> requirements
    );
}
