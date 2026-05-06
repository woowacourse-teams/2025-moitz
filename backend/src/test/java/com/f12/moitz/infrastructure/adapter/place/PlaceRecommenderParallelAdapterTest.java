package com.f12.moitz.infrastructure.adapter.place;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.place.PlaceRecommendationCriteria;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.RecommendedPlace;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapAsyncClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.MetaResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import com.f12.moitz.infrastructure.utils.KakaoPlaceMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PlaceRecommenderParallelAdapterTest {

    @Mock
    private KakaoMapAsyncClient kakaoMapAsyncClient;

    @Test
    @DisplayName("카카오맵 장소 추천 검색 요청 개수를 6개로 제한한다")
    void recommendPlaces_WithSixPlaceRecommendationCount() {
        final Place place = new Place("강남역", new Point(127.027, 37.497));
        final PlaceRecommenderParallelAdapter adapter = new PlaceRecommenderParallelAdapter(
                kakaoMapAsyncClient,
                new KakaoPlaceMapper()
        );
        given(kakaoMapAsyncClient.searchPlacesByAsync(any(SearchPlacesLimitQuantityRequest.class)))
                .willAnswer(invocation -> {
                    final SearchPlacesLimitQuantityRequest request = invocation.getArgument(0);
                    final int count = "PC방".equals(request.query()) ? 2 : 6;
                    return Mono.just(new KakaoApiResponse(
                            createDocuments(request.query(), count),
                            new MetaResponse(true, 0, 0, null)
                    ));
                });

        final Map<Place, RecommendedPlaces> recommendedPlaces = adapter.recommendPlaces(
                List.of(place),
                new PlaceRecommendationCriteria(List.of(RecommendCondition.PC_ROOM_KARAOKE), 6)
        );

        final ArgumentCaptor<SearchPlacesLimitQuantityRequest> captor =
                ArgumentCaptor.forClass(SearchPlacesLimitQuantityRequest.class);
        verify(kakaoMapAsyncClient, times(2)).searchPlacesByAsync(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(SearchPlacesLimitQuantityRequest::size)
                .containsOnly(6);
        assertThat(recommendedPlaces.get(place).getPlaces(RecommendCondition.PC_ROOM_KARAOKE))
                .hasSize(6)
                .extracting(RecommendedPlace::getName)
                .containsExactly(
                        "PC방 장소 0",
                        "노래방 장소 0",
                        "PC방 장소 1",
                        "노래방 장소 1",
                        "노래방 장소 2",
                        "노래방 장소 3"
                );
    }

    private List<DocumentResponse> createDocuments(final String keyword, final int count) {
        return IntStream.range(0, count)
                .mapToObj(index -> new DocumentResponse(
                        "FD6",
                        "음식점 > 카페",
                        "100",
                        keyword + " 장소 " + index,
                        "https://place.map.kakao.com/" + keyword + "-" + index,
                        "127.027",
                        "37.497",
                        "https://example.com/image-" + index + ".jpg"
                ))
                .toList();
    }

}
