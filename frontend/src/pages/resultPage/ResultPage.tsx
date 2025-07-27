import BottomSheet from '@features/bottomSheet/BottomSheet';
import Map from '@features/map/Map';

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
      <Map />
      <BottomSheet
        startingSpotNameList={startingSpotNameListMock}
        recommendedSpotItemList={recommendedSpotItemListMock}
      />
    </div>
  );
}

export default ResultPage;
