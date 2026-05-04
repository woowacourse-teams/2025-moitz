package com.f12.moitz.application;

import com.f12.moitz.domain.subway.repository.SubwayEdgeRepository;
import com.f12.moitz.domain.subway.SubwayEdges;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayEdgeService {

    private final SubwayEdgeRepository subwayEdgeRepository;

    public void saveAll(final SubwayEdges subwayEdges) {
        subwayEdgeRepository.saveAll(subwayEdges.getSubwayEdges());
    }

    public long getCount() {
        return subwayEdgeRepository.count();
    }

    public SubwayEdges getSubwayEdges() {
        return new SubwayEdges(new HashSet<>(subwayEdgeRepository.findAll()));
    }

}
