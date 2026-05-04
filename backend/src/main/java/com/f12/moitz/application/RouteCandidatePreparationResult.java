package com.f12.moitz.application;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;

public class RouteCandidatePreparationResult {

    private final List<Place> candidatePlaces;
    private final Map<Place, Routes> candidateRoutes;
    private final List<RouteCandidate> routeCandidates;

    public RouteCandidatePreparationResult(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        this.candidatePlaces = List.copyOf(candidatePlaces);
        this.candidateRoutes = Map.copyOf(candidateRoutes);
        this.routeCandidates = createRouteCandidates(this.candidatePlaces, this.candidateRoutes);
    }

    private List<RouteCandidate> createRouteCandidates(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        return candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .map(place -> new RouteCandidate(place, candidateRoutes.get(place)))
                .toList();
    }

    public List<Place> getCandidatePlaces() {
        return candidatePlaces;
    }

    public int getCandidatePlaceCount() {
        return candidatePlaces.size();
    }

    public long getRoutedPlaceCount() {
        return candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .count();
    }

    public Map<Place, Routes> getCandidateRoutes() {
        return candidateRoutes;
    }

    public List<RouteCandidate> getRouteCandidates() {
        return routeCandidates;
    }

}
