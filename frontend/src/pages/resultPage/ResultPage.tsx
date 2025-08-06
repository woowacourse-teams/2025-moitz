import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import useLocations from '@entities/hooks/useLocations';
import { RecommendedLocation } from '@entities/types/Location';

import { flex } from '@shared/styles/default.styled';

import { RequestBodyMock } from '@mocks/RequestBodyMock';
import startingLocationsMock from '@mocks/startingLocationsMock';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  const { data: locations, isLoading, isError } = useLocations(RequestBodyMock);

  if (isLoading) return <p>로딩중...</p>;
  if (isError) return <p>에러 발생!</p>;
  if (!locations || locations.length === 0) return <p>추천 결과가 없습니다.</p>;

  const recommendedLocations: RecommendedLocation[] = locations.map(
    (location) => ({
      id: location.id,
      index: location.index,
      x: location.x,
      y: location.y,
      name: location.name,
      avgMinutes: location.avgMinutes,
      isBest: location.isBest,
      description: location.description,
      reason: location.reason,
    }),
  );
  return (
    <div
      css={[
        flex({ direction: 'column', justify: 'flex-end' }),
        resultPage.base(),
      ]}
    >
      <Map
        startingLocations={startingLocationsMock}
        recommendedLocations={recommendedLocations}
      />
      <BottomSheet
        startingLocations={startingLocationsMock}
        recommendedLocations={recommendedLocations}
      />
    </div>
  );
}

export default ResultPage;
