/** @jsxImportSource @emotion/react */

import SpotItemList from '@features/recommendation/components/spotItemList/SpotItemList';

import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';
import { flex } from '@shared/styles/default.styled';

import { recommendedLocation } from '@shared/types/recommendedLocation';
import { startingLocation } from '@shared/types/startingLocation';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  startingLocations: startingLocation[];
  recommendedLocations: recommendedLocation[];
}

function BottomSheet({
  startingLocations,
  recommendedLocations,
}: BottomSheetProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), bottomSheet.base()]}>
      <div css={[flex({ direction: 'column', gap: 20 }), bottomSheet.scroll()]}>
        <StartingSpotWrapper startingLocations={startingLocations} />
        <SpotItemList recommendedLocations={recommendedLocations} />
      </div>
    </div>
  );
}

export default BottomSheet;
