package com.f12.moitz.infrastructure.persistence;

import com.f12.moitz.domain.subway.SubwayEdge;
import com.f12.moitz.domain.subway.repository.SubwayEdgeRepository;
import com.f12.moitz.infrastructure.persistence.repository.SubwayEdgeMongoRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class SubwayEdgeRepositoryAdapter implements SubwayEdgeRepository {

    private final SubwayEdgeMongoRepository subwayEdgeMongoRepository;

    public SubwayEdgeRepositoryAdapter(final SubwayEdgeMongoRepository subwayEdgeMongoRepository) {
        this.subwayEdgeMongoRepository = subwayEdgeMongoRepository;
    }

    @Override
    public void saveAll(final Set<SubwayEdge> subwayEdges) {
        subwayEdgeMongoRepository.saveAll(subwayEdges.stream()
                .map(SubwayEdgeDocument::fromSubwayEdge)
                .toList());
    }

    @Override
    public long count() {
        return subwayEdgeMongoRepository.count();
    }

    @Override
    public List<SubwayEdge> findAll() {
        return subwayEdgeMongoRepository.findAll().stream()
                .map(SubwayEdgeDocument::toSubwayEdge)
                .toList();
    }

}
