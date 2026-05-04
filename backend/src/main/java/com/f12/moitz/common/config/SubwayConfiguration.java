package com.f12.moitz.common.config;

import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.repository.SubwayEdgeRepository;
import com.f12.moitz.domain.subway.route.SubwayRouteCalculator;
import java.util.HashSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubwayConfiguration {

    @Bean
    public SubwayEdges subwayEdges(final SubwayEdgeRepository subwayEdgeRepository) {
        return new SubwayEdges(new HashSet<>(subwayEdgeRepository.findAll()));
    }

    @Bean
    public SubwayRouteCalculator subwayRouteCalculator(final SubwayEdges subwayEdges) {
        return new SubwayRouteCalculator(subwayEdges);
    }

}
