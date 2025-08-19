package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoPlaceFinderAdapter implements PlaceFinder {

    private final KakaoMapClient kakaoMapClient;

    @Override
    public Place findPlaceByName(final String placeName) {
        return new Place(placeName, kakaoMapClient.searchPointBy(placeName));
    }

    @Override
    public List<Place> findPlacesByNames(final List<String> placeNames) {
        return placeNames.stream()
                .map(this::findPlaceByName)
                .toList();
    }

}
