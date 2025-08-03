/** @jsxImportSource @emotion/react */

import SpotItemList from '@features/recommendation/components/spotItemList/SpotItemList';

import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';
import { flex } from '@shared/styles/default.styled';

import { startingLocation } from '@shared/types/startingLocation';

import { RecommendedLocation } from '../../../../entities/types/Location';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  startingLocations: startingLocation[];
  recommendedLocations: RecommendedLocation[];
}

function BottomSheet({
  startingLocations,
  recommendedLocations,
}: BottomSheetProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), bottomSheet.base()]}>
      <StartingSpotWrapper
        startingLocations={startingLocations}
      ></StartingSpotWrapper>
      <SpotItemList recommendedLocations={recommendedLocations} />
    </div>
  );
}

export default BottomSheet;
