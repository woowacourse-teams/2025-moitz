import SpotItemList from '@features/recommendation/components/spotItemList/SpotItemList';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';
import { flex } from '@shared/styles/default.styled';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  startingLocations: StartingPlace[];
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
