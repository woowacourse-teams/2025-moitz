import useLocations from '@entities/hooks/useLocations';
import { RecommendedLocation } from '@entities/types/Location';

import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import { flex } from '@shared/styles/default.styled';

import startingLocationsMock from '@mocks/startingLocationsMock';

import * as resultPage from './resultPage.styled';

const requestBody = {
  startingPlaceNames: ['강변역', '동대문역', '서울대입구역'],
  meetingTime: '14:00',
  requirement: '노래방은 있었으면 좋겠어요!',
};

function ResultPage() {
  const { data: locations, loading, error } = useLocations(requestBody);

  if (loading) return <p>로딩중...</p>;
  if (error) return <p>에러 발생!</p>;
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
