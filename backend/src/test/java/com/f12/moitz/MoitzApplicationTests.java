package com.f12.moitz;

import com.f12.moitz.application.subway.SetupService;
import com.f12.moitz.domain.subway.repository.SubwayEdgeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class MoitzApplicationTests {

    @MockitoBean
    private SubwayEdgeRepository subwayEdgeRepository;

    @MockitoBean
    private SetupService setupService;

    @Test
    void contextLoads() {
    }

}
