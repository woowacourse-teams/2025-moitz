package com.f12.moitz.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.PlaceService;
import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.client.open.OpenApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubwayMapBuilderTest {

    @Autowired
    private OpenApiClient openApiClient;
    @Autowired
    private SubwayStationRepository subwayStationRepository;
    @Autowired
    private PlaceService placeService;

    @DisplayName("지하철 노선도와 지하철역 Place를 업데이트한다")
    @Test
    void updateSubwayMap() {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JSR310 모듈 등록

        SubwayMapBuilder subwayMapBuilder = new SubwayMapBuilder(subwayStationRepository, openApiClient);

        // When
        Map<String, SubwayStation> subwayMap = subwayMapBuilder.buildAndSaveToMongo();

        placeService.saveIfAbsent(new ArrayList<>(subwayMap.keySet()));

        // Then
        assertThat(subwayMap).isNotEmpty();
    }

}
