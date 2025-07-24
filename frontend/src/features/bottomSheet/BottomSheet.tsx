/** @jsxImportSource @emotion/react */

import SpotItemList from '@features/spotItemList/SpotItemList';

import StartSpotWrapper from '@shared/components/startSpotWrapper/StartSpotWrapper';

import { nameInfo } from '@shared/types/nameInfo';
import { spotItem } from '@shared/types/spotItem';

import spotItemListMock from '../../mocks/spotItemListMock';
import { flex } from '../../shared/styles/default.styled';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  nameList: nameInfo[];
  itemList: spotItem[];
}

function BottomSheet({
  nameList,
  itemList = spotItemListMock,
}: BottomSheetProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), bottomSheet.base()]}>
      <StartSpotWrapper nameList={nameList}></StartSpotWrapper>
      <SpotItemList itemList={itemList} />
    </div>
  );
}

export default BottomSheet;
