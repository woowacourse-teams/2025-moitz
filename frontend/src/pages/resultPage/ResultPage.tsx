import { useCallback, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router';

import FallBackPage from '@pages/fallBackPage/FallBackPage';

import BaseLoading from '@features/loading/components/baseLoading/BaseLoading';
import Header from '@features/map/components/header/Header';
import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import { useLocationsContext } from '@entities/location/contexts/useLocationsContext';
import useSelectedRecommendedLocation from '@entities/location/hooks/useSelectedLocation';
import { RecommendedLocation } from '@entities/location/types/Location';

import { flex } from '@shared/styles/default.styled';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const {
    data: location,
    isLoading,
    getRecommendationResult,
  } = useLocationsContext();

  const fetchResult = useCallback(async () => {
    try {
      await getRecommendationResult(id);
    } catch {
      navigate('/');
    }
  }, []);

  useEffect(() => {
    if (!id) {
      navigate('/');
      return;
    }
    if (!location || location.recommendedLocations.length === 0) {
      fetchResult();
    }
  }, [id, location]);

  const { selectedLocation, changeSelectedLocation } =
    useSelectedRecommendedLocation();

  const handleSpotClick = (location: RecommendedLocation) => {
    changeSelectedLocation(location);
  };

  if (isLoading) {
    return <BaseLoading />;
  }

  if (!location || location.recommendedLocations.length === 0)
    return (
      <FallBackPage
        reset={() => navigate('/')}
        error={new Error('추천 결과가 없습니다.')}
        text="홈으로 돌아가기"
      />
    );

  const { startingPlaces, recommendedLocations } = location;

  return (
    <div css={resultPage.container()}>
      <Header
        selectedLocation={selectedLocation}
        onLocationChange={changeSelectedLocation}
      />
      <div
        css={[
          flex({ direction: 'column', justify: 'flex-end' }),
          resultPage.base(),
        ]}
      >
        <Map
          startingLocations={startingPlaces}
          recommendedLocations={recommendedLocations}
          selectedLocation={selectedLocation}
          changeSelectedLocation={changeSelectedLocation}
        />
        <BottomSheet
          startingLocations={location.startingPlaces}
          recommendedLocations={location.recommendedLocations}
          conditionID={location.requirement}
          selectedLocation={selectedLocation}
          handleSpotClick={handleSpotClick}
        />
      </div>
    </div>
  );
}

export default ResultPage;
