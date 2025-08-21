package com.f12.moitz.ui;

import com.f12.moitz.application.RecommendationService;
import com.f12.moitz.application.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendations")
public class RecommendationController implements SwaggerRecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationCreateResponse> recommendLocations(@RequestBody RecommendationRequest request) {
        return ResponseEntity.status(201).body(new RecommendationCreateResponse(recommendationService.recommendLocation(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationsResponse> getRecommendationResult(@PathVariable("id") String id){
        RecommendationsResponse response = recommendationService.findResultById(id);
        return ResponseEntity.ok().body(response);
    }

}
