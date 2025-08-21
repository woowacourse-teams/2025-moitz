package com.f12.moitz.ui;

import com.f12.moitz.application.dto.temp.MockRecommendationResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.temp.RecommendationsResponse;
import com.f12.moitz.common.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "지역 추천", description = "추천 API")
public interface SwaggerLocationController {

    @Operation(summary = "지역 추천 API", description = "만남 지역을 추천합니다.", responses = {
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
    ResponseEntity<RecommendationsResponse> recommendLocations(@RequestBody RecommendationRequest request);

    @Operation(summary = "지역 추천 테스트 API", description = "테스트 결과를 조회합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "결과 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = MockRecommendationResponse.class))
                    )
            )
    })
    ResponseEntity<MockRecommendationResponse> mockRecommend(@RequestBody RecommendationRequest request);

}
