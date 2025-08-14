import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import BottomSheetDetail from '../bottomSheetDetail/BottomSheetDetail';
import BottomSheetList from '../bottomSheetList/BottomSheetList';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  selectedLocation: RecommendedLocation | null;
  handleSpotClick: (spot: RecommendedLocation) => void;
}

function BottomSheet({
  startingLocations,
  recommendedLocations,
  selectedLocation,
  handleSpotClick,
}: BottomSheetProps) {
  return (
    <div
      css={[
        flex({ direction: 'column', gap: 20 }),
        shadow.bottom_sheet,
        bottomSheet.container(),
      ]}
    >
      <div
        css={[
          flex({ direction: 'column', gap: 20 }),
          scroll,
          bottomSheet.content(),
        ]}
      >
        {!selectedLocation && (
          <BottomSheetList
            startingPlaces={startingLocations}
            recommendedLocations={recommendedLocations}
            onSpotClick={handleSpotClick}
          />
        )}
        {selectedLocation && (
          <BottomSheetDetail
            startingPlaces={startingLocations}
            selectedLocation={selectedLocation}
          />
        )}
      </div>
    </div>
  );
}

export default BottomSheet;
