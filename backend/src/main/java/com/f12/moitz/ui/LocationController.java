package com.f12.moitz.ui;

import com.f12.moitz.application.LocationApplicationService;
import com.f12.moitz.application.dto.LocationRecommendRequest;
import com.f12.moitz.application.dto.LocationRecommendResponse;
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
public class LocationController implements SwaggerLocationController {

    private final LocationApplicationService locationService;

    @PostMapping
    public ResponseEntity<List<LocationRecommendResponse>> recommendLocations(@RequestBody LocationRecommendRequest request) {
        return ResponseEntity.ok(locationService.recommendLocation(request));
    }

}
