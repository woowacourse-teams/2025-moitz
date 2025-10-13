import { useMemo } from 'react';

import FallBackPage from '@pages/fallBackPage/FallBackPage';

import { useCustomOverlays } from '@features/map/hooks/useCustomOverlays';
import { useNaverMapLoader } from '@features/map/hooks/useNaverMapLoader';
import { SelectedLocation } from '@features/recommendation/types/SelectedLocation';

import {
  RecommendedLocation,
  StartingPlace,
} from '@entities/location/types/Location';

import Skeleton from '@shared/components/skeleton/Skeleton';

import * as map from './map.styled';

interface MapProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  selectedLocation: SelectedLocation;
  changeSelectedLocation: (location: SelectedLocation) => void;
}

function Map({
  startingLocations,
  recommendedLocations,
  selectedLocation,
  changeSelectedLocation,
}: MapProps) {
  // 네이버 지도 API 로딩 상태 관리
  const { isScriptLoaded, isLoading, errorMessage } = useNaverMapLoader();

  const emptyProps = useMemo(
    () => ({
      startingLocations: [],
      recommendedLocations: [],
      selectedLocation: null,
      changeSelectedLocation: () => {},
    }),
    [],
  );

  // 스크립트가 로드된 후에만 지도 훅 사용
  const mapRef = useCustomOverlays(
    isScriptLoaded
      ? {
          startingLocations,
          recommendedLocations,
          selectedLocation,
          changeSelectedLocation,
        }
      : emptyProps,
  );

  // 로딩 중일 때
  if (isLoading) {
    return <Skeleton />;
  }

  // 에러가 발생했을 때
  if (errorMessage) {
    return (
      <FallBackPage
        reset={() => window.location.reload()}
        error={new Error(`지도 로딩 실패: ${errorMessage}`)}
        text="페이지 새로고침"
      />
    );
  }

  // 스크립트가 로드되었을 때만 지도 렌더링
  return <div ref={mapRef} css={map.container()} />;
}

export default Map;
