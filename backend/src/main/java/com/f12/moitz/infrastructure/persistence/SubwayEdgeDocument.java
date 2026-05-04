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
public class SubwayEdgeDocument {

    private final SubwayStation subwayStation;
    private final Set<Edge> edges;

    public SubwayEdge toSubwayEdge() {
        final SubwayEdge subwayEdge = new SubwayEdge(subwayStation);
        edges.forEach(subwayEdge::addEdge);
        return subwayEdge;
    }

    public static SubwayEdgeDocument fromSubwayEdge(final SubwayEdge subwayEdge) {
        return new SubwayEdgeDocument(
                subwayEdge.getSubwayStation(),
                new HashSet<>(subwayEdge.getEdges())
        );
    }

}
