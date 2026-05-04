package com.f12.moitz.infrastructure.persistence;

import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayEdge;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@RequiredArgsConstructor
@Getter
@Document(collection = "subway-edge")
public class SubwayEdgeEntity {

    private final SubwayStation subwayStation;
    private final Set<Edge> edges;

    public SubwayEdge toDomain() {
        final SubwayEdge subwayEdge = new SubwayEdge(subwayStation);
        edges.forEach(subwayEdge::addEdge);
        return subwayEdge;
    }

    public static SubwayEdgeEntity fromDomain(final SubwayEdge subwayEdge) {
        return new SubwayEdgeEntity(
                subwayEdge.getSubwayStation(),
                new HashSet<>(subwayEdge.getEdges())
        );
    }

}
