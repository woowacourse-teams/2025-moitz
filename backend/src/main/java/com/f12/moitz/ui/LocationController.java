package com.f12.moitz.ui;

import com.f12.moitz.application.LocationService;
import com.f12.moitz.ui.dto.LocationRecommendRequest;
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
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<List<PlaceResponse>> recommendLocations(@RequestBody LocationRecommendRequest request) {
        return ResponseEntity.ok(locationService.recommendLocation(request));
    }

}
