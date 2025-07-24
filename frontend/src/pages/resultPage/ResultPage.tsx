import { useLocation } from 'react-router';

import BottomSheet from '@features/bottomSheet/BottomSheet';
import Map from '@features/map/Map';

import { flex } from '@shared/styles/default.styled';

import { nameInfo } from '@shared/types/nameInfo';

import spotItemListMock from '../../mocks/spotItemListMock';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  const location = useLocation();
  const state = location.state as { startingPlaces: nameInfo[] };

  return (
    <div
      css={[
        flex({ direction: 'column', justify: 'flex-end' }),
        resultPage.base(),
      ]}
    >
      <Map />
      <BottomSheet
        nameList={state?.startingPlaces}
        itemList={spotItemListMock}
      />
    </div>
  );
}

export default ResultPage;
