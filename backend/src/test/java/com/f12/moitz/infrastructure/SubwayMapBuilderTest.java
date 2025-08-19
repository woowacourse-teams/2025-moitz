package com.f12.moitz.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.client.open.OpenApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private SubwayStationRepository subwayStationRepository;

    @DisplayName("지하철 노선도를 업데이트한다")
    @Test
    void updateSubwayMap() {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JSR310 모듈 등록

        SubwayMapBuilder subwayMapBuilder = new SubwayMapBuilder(subwayStationRepository,openApiClient);

        // When
        Map<String, SubwayStation> subwayMap = subwayMapBuilder.buildAndSaveToMongo();

        // Then
        assertThat(subwayMap).isNotEmpty();
    }
}
