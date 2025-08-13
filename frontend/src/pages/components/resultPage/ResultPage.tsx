import useSelectedRecommendedLocation from '@pages/hooks/useSelectedLocation';

import ProgressLoading from '@features/loading/components/progressLoading/ProgressLoading';
import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import { useLocationsContext } from '@entities/contexts/useLocationsContext';
import { RecommendedLocation } from '@entities/types/Location';

import { flex } from '@shared/styles/default.styled';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  const { data: location, isLoading, isError } = useLocationsContext();
  const { selectedLocation, changeSelectedLocation } =
    useSelectedRecommendedLocation();

  const handleSpotClick = (location: RecommendedLocation) => {
    changeSelectedLocation(location);
  };

  const handleBackButtonClick = () => {
    changeSelectedLocation(null);
  };

  if (isLoading) return <ProgressLoading />;
  if (isError) return <p>에러 발생!</p>;
  if (!location || location.recommendedLocations.length === 0)
    return <p>추천 결과가 없습니다.</p>;

  const { startingPlaces, recommendedLocations } = location;

  return (
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
        handleBackButtonClick={handleBackButtonClick}
      />
      <BottomSheet
        startingLocations={location.startingPlaces}
        recommendedLocations={location.recommendedLocations}
        selectedLocation={selectedLocation}
        handleSpotClick={handleSpotClick}
      />
    </div>
  );
}

export default ResultPage;
