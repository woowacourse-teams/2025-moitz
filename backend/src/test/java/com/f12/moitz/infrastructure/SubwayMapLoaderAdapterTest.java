package com.f12.moitz.infrastructure;

import com.f12.moitz.application.subway.SubwayStationService;
import com.f12.moitz.domain.subway.repository.SubwayStationRepository;
import com.f12.moitz.infrastructure.adapter.subway.SubwayMapLoaderAdapter;
import com.f12.moitz.infrastructure.client.open.OpenApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubwayMapLoaderAdapterTest {

    @Autowired
    private OpenApiClient openApiClient;
    @Autowired
    private SubwayStationRepository subwayStationRepository;
    @Autowired
    private SubwayStationService subwayStationService;

    @DisplayName("지하철 노선도와 지하철역 Place를 업데이트한다")
    @Test
    void updateSubwayMap() {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JSR310 모듈 등록

        SubwayMapLoaderAdapter subwayMapLoaderAdapter = new SubwayMapLoaderAdapter(openApiClient);

        // When
        // Map<String, SubwayStation> subwayMap = subwayMapLoaderAdapter.loadSubwayMap();

        // subwayStationService.saveIfAbsent(new ArrayList<>(subwayMap.keySet()));

        // Then
        // assertThat(subwayMap).isNotEmpty();
    }

}
