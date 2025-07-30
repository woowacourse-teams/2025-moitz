/** @jsxImportSource @emotion/react */

import SpotItemList from '@features/recommendation/components/spotItemList/SpotItemList';

import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';
import { flex } from '@shared/styles/default.styled';

import { recommendedSpotItem } from '@shared/types/recommendedSpotItem';
import { startingSpotName } from '@shared/types/startingSpotName';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  startingSpotNameList: startingSpotName[];
  recommendedSpotItemList: recommendedSpotItem[];
}

function BottomSheet({
  startingSpotNameList,
  recommendedSpotItemList,
}: BottomSheetProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), bottomSheet.base()]}>
      <StartingSpotWrapper
        startingSpotNameList={startingSpotNameList}
      ></StartingSpotWrapper>
      <SpotItemList recommendedSpotItemList={recommendedSpotItemList} />
    </div>
  );
}

export default BottomSheet;
