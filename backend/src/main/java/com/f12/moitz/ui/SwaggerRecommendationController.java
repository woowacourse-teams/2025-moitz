package com.f12.moitz.ui;

import com.f12.moitz.application.dto.recommendation.RecommendationCreateResponse;
import com.f12.moitz.application.dto.recommendation.RecommendationRequest;
import com.f12.moitz.application.dto.recommendation.RecommendationResultResponse;
import com.f12.moitz.application.dto.vote.VoteRequest;
import com.f12.moitz.application.dto.vote.VotesResponse;
import com.f12.moitz.common.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "추천", description = "추천 API")
public interface SwaggerRecommendationController {

    @Operation(summary = "지역 추천 API", description = "만남 지역을 추천 후 결과 아이디를 반환합니다.", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "결과 생성",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationCreateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<RecommendationCreateResponse> recommendLocations(@RequestBody RecommendationRequest request);

    @Operation(summary = "추천 결과 조회 API", description = "추천 결과를 조회합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecommendationResultResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "없는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "C0004", description = "존재하지 않거나 유효 기간이 만료된 추천 결과입니다.")
                    )
            )
    })
    ResponseEntity<RecommendationResultResponse> getRecommendationResult(@PathVariable("id") String id);

    @Operation(summary = "투표 조회 API", description = "추천 역의 투표 현황을 조회합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VotesResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "없는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "C0004", description = "존재하지 않거나 유효 기간이 만료된 추천 결과입니다.")
                    )
            )
    })
    ResponseEntity<List<VotesResponse>> getAllVoteResults(@PathVariable("id") final String id);

    @Operation(summary = "투표 수행 API", description = "추천 받은 역 중 한 곳에 1표를 추가합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "액션 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VotesResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "없는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "C0004", description = "존재하지 않거나 유효 기간이 만료된 추천 결과입니다."),
                                    @ExampleObject(name = "C0005", description = "유효하지 않은 추천 지역입니다.")
                            }
                    )
            )
    })
    ResponseEntity<VotesResponse> voteOnCandidate(
            @PathVariable("id") final String id,
            @RequestBody final VoteRequest request
    );

}
