import ProgressLoading from '@features/loading/components/progressLoading/ProgressLoading';
import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import useLocations from '@entities/hooks/useLocations';
import { RecommendedLocation } from '@entities/types/Location';

import { flex } from '@shared/styles/default.styled';

import { StartingPlacesMock } from '@mocks/LocationsMock';
import { LocationsRequestBodyMock } from '@mocks/LocationsRequestBodyMock';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  const {
    data: location,
    isLoading,
    isError,
  } = useLocations(LocationsRequestBodyMock);

  if (isLoading) return <ProgressLoading isLoading={isLoading} />;
  if (isError) return <p>에러 발생!</p>;
  if (!location || location.recommendedLocations.length === 0)
    return <p>추천 결과가 없습니다.</p>;

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
        startingLocations={StartingPlacesMock}
        recommendedLocations={recommendedLocations}
      />
      <BottomSheet
        startingLocations={StartingPlacesMock}
        recommendedLocations={recommendedLocations}
      />
    </div>
  );
}

export default ResultPage;
