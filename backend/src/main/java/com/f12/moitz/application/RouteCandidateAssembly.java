package com.f12.moitz.application;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;

public class RouteCandidateAssembly {

    private final List<Place> candidatePlaces;
    private final Map<Place, Routes> candidateRoutes;
    private final List<RouteCandidate> routeCandidates;

    public RouteCandidateAssembly(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final List<RouteCandidate> routeCandidates
    ) {
        this.candidatePlaces = List.copyOf(candidatePlaces);
        this.candidateRoutes = Map.copyOf(candidateRoutes);
        this.routeCandidates = List.copyOf(routeCandidates);
    }

    public List<Place> getCandidatePlaces() {
        return candidatePlaces;
    }

    public Map<Place, Routes> getCandidateRoutes() {
        return candidateRoutes;
    }

    public List<RouteCandidate> getRouteCandidates() {
        return routeCandidates;
    }

}
