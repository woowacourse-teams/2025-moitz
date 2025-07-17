package com.f12.moitz.ui;

import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.RecommendationsResponse;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationPreview;
import com.f12.moitz.ui.dto.LocationRecommendRequest;
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

    private final GoogleGeminiClient googleGeminiClient;

    @PostMapping
    public ResponseEntity<RecommendationsResponse> generateData(@RequestBody LocationRecommendRequest request) {
        return ResponseEntity.ok(googleGeminiClient.generateDetailResponse(request.stations(), request.additionalConditions()));
    }

    @PostMapping("/preview")
    public ResponseEntity<RecommendedLocationPreview> generatePreviewData(@RequestBody LocationRecommendRequest request) {
        return ResponseEntity.ok(googleGeminiClient.generateBriefResponse(request.stations(), request.additionalConditions()));
    }

}
