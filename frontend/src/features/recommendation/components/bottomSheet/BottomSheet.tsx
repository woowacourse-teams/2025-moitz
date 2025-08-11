import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import Detail from '../detail/Detail';
import List from '../list/List';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  currentView: View;
  selectedLocation: RecommendedLocation | null;
  handleSpotClick: (spot: RecommendedLocation) => void;
}

function BottomSheet({
  startingLocations,
  recommendedLocations,
  currentView,
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
        {currentView === 'list' ? (
          <>
            <List
              startingPlaces={startingLocations}
              recommendedLocations={recommendedLocations}
              onSpotClick={handleSpotClick}
            />
          </>
        ) : (
          <Detail
            startingPlaces={startingLocations}
            selectedLocation={selectedLocation}
          />
        )}
      </div>
    </div>
  );
}

export default BottomSheet;
