import Map from '@features/map/components/map/Map';
import BottomSheet from '@features/recommandation/components/bottomSheet/BottomSheet';

import { flex } from '@shared/styles/default.styled';

import recommendedSpotItemListMock from '../../mocks/recommendedSpotItemListMock';
import startingSpotNameListMock from '../../mocks/startingSpotNameListMock';

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
        startingSpotNameList={startingSpotNameListMock}
        recommendedSpotItemList={recommendedSpotItemListMock}
      />
      <BottomSheet
        startingSpotNameList={startingSpotNameListMock}
        recommendedSpotItemList={recommendedSpotItemListMock}
      />
    </div>
  );
}

export default ResultPage;
