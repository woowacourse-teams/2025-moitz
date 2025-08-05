import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommendation/components/bottomSheet/BottomSheet';

import { flex } from '@shared/styles/default.styled';

import recommendedLocationsMock from '@mocks/recommendedLocationsMock';
import startingLocationsMock from '@mocks/startingLocationsMock';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  return (
    <div
      css={[
        flex({ direction: 'column', justify: 'flex-end' }),
        resultPage.base(),
      ]}
    >
      <Map
        startingLocations={startingLocationsMock}
        recommendedLocations={recommendedLocationsMock}
      />
      <BottomSheet
        startingLocations={startingLocationsMock}
        recommendedLocations={recommendedLocationsMock}
      />
    </div>
  );
}

export default ResultPage;
