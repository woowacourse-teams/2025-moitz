package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.subway.SubwayEdge;
import java.util.List;
import java.util.Set;

public interface SubwayEdgeRepository {

    void saveAll(Set<SubwayEdge> subwayEdges);

    long count();

    List<SubwayEdge> findAll();

}
