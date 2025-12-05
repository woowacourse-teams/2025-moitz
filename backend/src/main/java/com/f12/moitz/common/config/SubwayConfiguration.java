package com.f12.moitz.common.config;

import com.f12.moitz.application.SubwayEdgeService;
import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.SubwayRouteCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubwayConfiguration {

    @Bean
    public SubwayEdges subwayEdges(final SubwayEdgeService subwayEdgeService) {
        return subwayEdgeService.getSubwayEdges();
    }

    @Bean
    public SubwayRouteCalculator subwayRouteCalculator(final SubwayEdges subwayEdges) {
        return new SubwayRouteCalculator(subwayEdges);
    }

}
