package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.persistence.SubwayStationEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@ExtendWith(MockitoExtension.class)
class SubwayStationServiceTest {

    @Mock
    private SubwayStationRepository subwayStationRepository;

    @InjectMocks
    private SubwayStationService subwayStationService;

    @DisplayName("'이수역' 또는 '총신대입구역'으로 지하철 역 검색 시 총신대입구(이수)역을 반환한다.")
    @Test
    void convertName() {
        // Given
        final String expectedName = "총신대입구(이수)역";
        final SubwayStationEntity expectedStation = new SubwayStationEntity(expectedName, new GeoJsonPoint(125, 34));

        Mockito.when(subwayStationRepository.findByName("총신대입구(이수)역")).thenReturn(Optional.of(expectedStation));

        // When
        final Optional<SubwayStation> station1 = subwayStationService.findByName("이수역");
        final Optional<SubwayStation> station2 = subwayStationService.findByName("총신대입구역");

        // Then
        assertThat(station1).contains(expectedStation.toSubwayStation());
        assertThat(station1.get().getName()).isEqualTo(expectedName);
        assertThat(station2).contains(expectedStation.toSubwayStation());
        assertThat(station2.get().getName()).isEqualTo(expectedName);
    }

    @DisplayName("대표 역명이 없으면 입력 역명으로 다시 지하철 역을 검색한다")
    @Test
    void findByName_FallbackAliasName() {
        // Given
        final SubwayStationEntity expectedStation = new SubwayStationEntity("이수역", new GeoJsonPoint(125, 34));
        Mockito.when(subwayStationRepository.findByName("총신대입구(이수)역")).thenReturn(Optional.empty());
        Mockito.when(subwayStationRepository.findByName("이수역")).thenReturn(Optional.of(expectedStation));

        // When
        final Optional<SubwayStation> station = subwayStationService.findByName("이수역");

        // Then
        assertThat(station).contains(expectedStation.toSubwayStation());
    }

    @DisplayName("출발역 중심점과 반경으로 후보역을 검색한다")
    @Test
    void generateCandidatePlace() {
        // Given
        final SubwayStation gangnam = new SubwayStation("강남역", new Point(127.0, 37.0));
        final SubwayStation yeoksam = new SubwayStation("역삼역", new Point(129.0, 39.0));
        final SubwayStationEntity seolleung = new SubwayStationEntity("선릉역", new GeoJsonPoint(128.0, 38.0));
        Mockito.when(subwayStationRepository.findByPointNear(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(seolleung));

        // When
        final List<SubwayStation> candidatePlaces = subwayStationService.generateCandidatePlace(
                List.of(gangnam, yeoksam),
                10
        );

        // Then
        assertThat(candidatePlaces).containsExactly(seolleung.toSubwayStation());

        final ArgumentCaptor<org.springframework.data.geo.Point> pointCaptor = ArgumentCaptor.forClass(
                org.springframework.data.geo.Point.class
        );
        final ArgumentCaptor<Distance> distanceCaptor = ArgumentCaptor.forClass(Distance.class);
        Mockito.verify(subwayStationRepository).findByPointNear(pointCaptor.capture(), distanceCaptor.capture());
        assertThat(pointCaptor.getValue().getX()).isEqualTo(128.0);
        assertThat(pointCaptor.getValue().getY()).isEqualTo(38.0);
        assertThat(distanceCaptor.getValue().getValue()).isEqualTo(10.0);
    }

    @DisplayName("후보역 생성 시 출발역 목록은 비어있거나 null일 수 없다")
    @Test
    void generateCandidatePlace_ValidateStartingStations() {
        assertThatThrownBy(() -> subwayStationService.generateCandidatePlace(null, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발 지하철역 목록은 비어있거나 null일 수 없습니다.");
        assertThatThrownBy(() -> subwayStationService.generateCandidatePlace(List.of(), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발 지하철역 목록은 비어있거나 null일 수 없습니다.");
    }

    @DisplayName("후보역 생성 시 검색 반경은 0보다 커야 한다")
    @Test
    void generateCandidatePlace_ValidateDistanceValue() {
        final SubwayStation station = new SubwayStation("강남역", new Point(127.0, 37.0));

        assertThatThrownBy(() -> subwayStationService.generateCandidatePlace(List.of(station), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("후보역 검색 반경은 0보다 커야 합니다.");
        assertThatThrownBy(() -> subwayStationService.generateCandidatePlace(List.of(station), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("후보역 검색 반경은 0보다 커야 합니다.");
    }

}
