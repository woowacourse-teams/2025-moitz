package com.f12.moitz.ui;

import com.f12.moitz.application.dto.LocationRecommendRequest;
import com.f12.moitz.application.dto.LocationRecommendResponse;
import com.f12.moitz.common.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "지역 추천", description = "추천 API")
public interface SwaggerLocationController {

    @Operation(summary = "지역 추천 API", description = "만남 지역을 추천합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "지역 추천 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = LocationRecommendResponse.class))
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
    ResponseEntity<List<LocationRecommendResponse>> recommendLocations(@RequestBody LocationRecommendRequest request);

}
