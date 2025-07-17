package com.f12.moitz.ui;

import com.f12.moitz.application.LocationCalculator;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.ui.dto.PlaceRequest;
import com.f12.moitz.ui.dto.PlaceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/places")
public class PlaceController {

    private final KakaoMapClient kakaoMapClient;
    private final LocationCalculator locationCalculator;

    @PostMapping
    public ResponseEntity<List<PlaceResponse>> recommendPlaces(@RequestBody List<PlaceRequest> requests) {
        // TODO: 계산 로직 이동
        PlaceRequest calculatedPoint = locationCalculator.calculateCenterPoint(requests);
        String placeName = kakaoMapClient.searchPlaceBy(calculatedPoint.longitude(), calculatedPoint.latitude());
        System.out.println(placeName + " - 위도 : " + calculatedPoint.latitude() + ", 경도 : " + calculatedPoint.longitude());

        // TODO: 임시 데이터 제거
        return ResponseEntity.ok(generateTempPlaces());
    }

    private List<PlaceResponse> generateTempPlaces() {
        return List.of(
                new PlaceResponse(37.5665, 126.978, "잠실 메가커피"),
                new PlaceResponse(37.5651, 126.989, "메가박스 홍대입구"),
                new PlaceResponse(37.5700, 126.976, "컴포즈 커피 강남역점")
        );
    }
}
