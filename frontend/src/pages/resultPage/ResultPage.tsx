import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import { useLocationsContext } from '@entities/contexts/useLocationsContext';
import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex } from '@shared/styles/default.styled';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  const { data: location, isLoading, isError } = useLocationsContext();

  if (isLoading) return <p>로딩중...</p>;
  if (isError) return <p>에러 발생!</p>;
  if (!location || location.recommendedLocations.length === 0)
    return <p>추천 결과가 없습니다.</p>;

  const startingPlaces: StartingPlace[] = location.startingPlaces.map(
    (location) => {
      return {
        id: location.id,
        index: location.index,
        x: location.x,
        y: location.y,
        name: location.name,
      };
    },
  );

  const recommendedLocations: RecommendedLocation[] =
    location.recommendedLocations.map((location) => {
      return {
        id: location.id,
        index: location.index,
        x: location.x,
        y: location.y,
        name: location.name,
        avgMinutes: location.avgMinutes,
        isBest: location.isBest,
        description: location.description,
        reason: location.reason,
      };
    });

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
      />
      <BottomSheet
        startingLocations={startingPlaces}
        recommendedLocations={recommendedLocations}
      />
    </div>
  );
}

export default ResultPage;
