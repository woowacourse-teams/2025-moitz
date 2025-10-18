package com.f12.moitz.ui;

import com.f12.moitz.application.RecommendationService;
import com.f12.moitz.application.VoteService;
import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResultResponse;
import com.f12.moitz.application.dto.VoteRequest;
import com.f12.moitz.application.dto.VotesResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<RecommendationCreateResponse> recommendLocations(
            @RequestBody final RecommendationRequest request
    ) {
        return ResponseEntity.status(201).body(recommendationService.recommendLocation(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationResultResponse> getRecommendationResult(@PathVariable("id") final String id) {
        return ResponseEntity.ok().body(recommendationService.getById(id));
    }

    @GetMapping("/{id}/votes")
    public ResponseEntity<List<VotesResponse>> getAllVoteResults(@PathVariable("id") final String id) {
        return ResponseEntity.ok().body(voteService.getAllVotes(id));
    }

    @PatchMapping("/{id}/votes")
    public ResponseEntity<VotesResponse> voteOnCandidate(
            @PathVariable("id") final String id,
            @RequestBody final VoteRequest request
    ) {
        return ResponseEntity.ok().body(voteService.addVote(id, request.locationName()));
    }

}
