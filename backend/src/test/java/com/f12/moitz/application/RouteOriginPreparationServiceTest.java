package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouteOriginPreparationServiceTest {

    @Mock
    private SubwayStationService subwayStationService;

    @Test
    @DisplayName("출발지 이름 목록을 출발지와 경로 출발지로 준비한다")
    void prepare() {
        final RouteOriginPreparationService service = new RouteOriginPreparationService(subwayStationService);
        final SubwayStation gangnam = station("강남역");
        final SubwayStation yeoksam = station("역삼역");
        given(subwayStationService.findByName("강남역")).willReturn(Optional.of(gangnam));
        given(subwayStationService.findByName("역삼역")).willReturn(Optional.of(yeoksam));

        final RouteOriginPreparationResult result = service.prepare(List.of("강남역", "역삼역"));

        assertThat(result.getStartingPlaces()).containsExactly(gangnam, yeoksam);
        assertThat(result.getRouteOrigins().getOrigins()).containsExactly(gangnam, yeoksam);
    }

    @Test
    @DisplayName("출발지 이름을 찾을 수 없으면 제어된 예외를 반환한다")
    void prepare_ThrowsBadRequestWhenStartingPlaceNameIsInvalid() {
        final RouteOriginPreparationService service = new RouteOriginPreparationService(subwayStationService);
        given(subwayStationService.findByName("없는역")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.prepare(List.of("없는역")))
                .isInstanceOfSatisfying(BadRequestException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_START_LOCATION));
    }

    @Test
    @DisplayName("출발지 이름이 같은 장소로 해석되면 제어된 예외를 반환한다")
    void prepare_ThrowsBadRequestWhenResolvedStartingPlacesAreDuplicated() {
        final RouteOriginPreparationService service = new RouteOriginPreparationService(subwayStationService);
        final SubwayStation isu = station("이수역");
        given(subwayStationService.findByName("이수역")).willReturn(Optional.of(isu));
        given(subwayStationService.findByName("총신대입구역")).willReturn(Optional.of(isu));

        assertThatThrownBy(() -> service.prepare(List.of("이수역", "총신대입구역")))
                .isInstanceOfSatisfying(BadRequestException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_START_LOCATION));
    }

    private SubwayStation station(final String name) {
        return new SubwayStation(name, new Point(127.0, 37.0));
    }

}
