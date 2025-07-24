import BottomSheet from '@features/bottomSheet/BottomSheet';

import { flex } from '@shared/styles/default.styled';

import spotItemListMock from '../../mocks/spotItemListMock';

import * as resultPage from './resultPage.styled';

function ResultPage() {
  return (
    <div
      css={[
        flex({ direction: 'column', justify: 'flex-end' }),
        resultPage.base(),
      ]}
    >
      <BottomSheet itemList={spotItemListMock} />
    </div>
  );
}

export default ResultPage;
