package com.f12.moitz.ui;

import com.f12.moitz.application.dto.MockRecommendationResponse;
import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.common.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "추천", description = "추천 API")
public interface SwaggerRecommendationController {

    @Operation(summary = "지역 추천 API", description = "만남 지역을 추천 후 결과 아이디를 반환합니다.", responses = {
            @ApiResponse(
                    responseCode = "201",
                    description = "지역 추천 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = Map.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 에러",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<Map<String,String>> recommendLocations(@RequestBody RecommendationRequest request);

    @Operation(summary = "추천 결과 조회 API", description = "추천 결과를 조회합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "결과 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RecommendationsResponse.class))
                    )
            )
    })
    ResponseEntity<RecommendationsResponse> getRecommendationResult(@PathVariable("id") String id);

    @Operation(summary = "추천 생성 요청 API 테스트용", description = "추천 조건을 기반으로 추천 생성을 요청합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = RecommendationCreateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<RecommendationCreateResponse> mockRecommend(@RequestBody RecommendationRequest request);

    @Operation(summary = "추천 결과 조회 API 테스트용", description = "만남 지역 추천 결과를 확인합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 결과 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = MockRecommendationResponse.class)
                    )
            )
    })
    ResponseEntity<MockRecommendationResponse> mockGetRecommendation(@PathVariable("id") Long id);
}
