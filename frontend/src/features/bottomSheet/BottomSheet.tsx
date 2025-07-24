/** @jsxImportSource @emotion/react */

import SpotItemList from '@features/spotItemList/SpotItemList';

import StartSpotWrapper from '@shared/components/startSpotWrapper/StartSpotWrapper';

import { spotItem } from '@shared/types/spotItem';

import spotItemListMock from '../../mocks/spotItemListMock';
import startSpotNameListMock from '../../mocks/startSpotNameListMock';
import { flex } from '../../shared/styles/default.styled';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  itemList: spotItem[];
}

function BottomSheet({ itemList = spotItemListMock }: BottomSheetProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), bottomSheet.base()]}>
      <StartSpotWrapper nameList={startSpotNameListMock}></StartSpotWrapper>
      <SpotItemList itemList={itemList} />
    </div>
  );
}

export default BottomSheet;
