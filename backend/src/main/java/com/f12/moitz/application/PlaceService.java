package com.f12.moitz.application;

import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.repository.PlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceFinder placeFinder;

    public Place findByName(final String name) {
        return placeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("이름이 일치하는 Place가 존재하지 않습니다."));
    }

    public List<Place> findByNames(final List<String> names) {
        return names.stream()
                .map(this::findByName)
                .toList();
    }

    public int saveIfAbsent(final List<String> placeNames) {
        final List<Place> places = placeNames.stream()
                .filter(name -> !placeRepository.existsByName(name))
                .map(placeFinder::findPlaceByName)
                .toList();

        placeRepository.saveAll(places);

        return places.size();
    }

}
