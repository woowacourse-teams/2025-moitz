package com.f12.moitz.ui;

import com.f12.moitz.infrastructure.KakaoMapClient;
import com.f12.moitz.ui.dto.PlaceRequest;
import com.f12.moitz.ui.dto.PlaceResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
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

    @PostMapping
    public ResponseEntity<List<PlaceResponse>> recommendPlaces(@RequestBody List<PlaceRequest> requests) {
        // TODO: 계산 로직 이동
        PlaceRequest calculatedPoint = calculateCenterPoint(requests);
        String placeName = kakaoMapClient.searchPlaceBy(calculatedPoint.longitude(), calculatedPoint.latitude());
        System.out.println(placeName + " - 위도 : " + calculatedPoint.latitude() + ", 경도 : " + calculatedPoint.longitude());

        // TODO: 임시 데이터 제거
        return ResponseEntity.ok(generateTempPlaces());
    }

    private PlaceRequest calculateCenterPoint(List<PlaceRequest> places) {
        List<Coordinate> points = new ArrayList<>();

        for (PlaceRequest place : places) {
            points.add(new Coordinate(place.longitude(), place.latitude()));
        }

        Coordinate[] pointArray = points.toArray(new Coordinate[0]);
        Coordinate midpoint = calculateMidpoint(pointArray);

        return new PlaceRequest(midpoint.y, midpoint.x);
    }

    private Coordinate calculateMidpoint(Coordinate[] geoPoints) {
        double x = 0, y = 0, z = 0;
        int count = geoPoints.length;

        for (Coordinate geo : geoPoints) {
            Coordinate cart = toCartesian(geo.y, geo.x);
            x += cart.x;
            y += cart.y;
            z += cart.z;
        }

        x /= count;
        y /= count;
        z /= count;

        Coordinate avgCartesian = new Coordinate(x, y, z);
        return toGeoCoordinate(avgCartesian);
    }

    private Coordinate toCartesian(double latDeg, double lonDeg) {
        double latRad = Math.toRadians(latDeg);
        double lonRad = Math.toRadians(lonDeg);

        double x = Math.cos(latRad) * Math.cos(lonRad);
        double y = Math.cos(latRad) * Math.sin(lonRad);
        double z = Math.sin(latRad);

        return new Coordinate(x, y, z);
    }

    private Coordinate toGeoCoordinate(Coordinate coord) {
        double hyp = Math.sqrt(coord.x * coord.x + coord.y * coord.y);
        double latRad = Math.atan2(coord.z, hyp);
        double lonRad = Math.atan2(coord.y, coord.x);

        double latDeg = Math.toDegrees(latRad);
        double lonDeg = Math.toDegrees(lonRad);

        return new Coordinate(lonDeg, latDeg);
    }

    private List<PlaceResponse> generateTempPlaces() {
        return List.of(
                new PlaceResponse(37.5665, 126.978, "선릉역"),
                new PlaceResponse(37.5651, 126.989, "삼성역"),
                new PlaceResponse(37.5700, 126.976, "잠실역")
        );
    }
}
