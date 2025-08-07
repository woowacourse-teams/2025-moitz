import { useState } from 'react';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import Detail from '../detail/Detail';
import List from '../list/List';

import * as bottomSheet from './bottomSheet.styled';
type View = 'list' | 'detail';
interface BottomSheetProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
}

function BottomSheet({
  startingLocations,
  recommendedLocations,
}: BottomSheetProps) {
  const [currentView, setCurrentView] = useState<View>('list');
  const [selectedLocation, setSelectedLocation] =
    useState<RecommendedLocation | null>(null);

  const handleSpotClick = (spot: RecommendedLocation) => {
    setSelectedLocation(spot);
    setCurrentView('detail');
  };

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
              startingLocations={startingLocations}
              recommendedLocations={recommendedLocations}
              onSpotClick={handleSpotClick}
            />
          </>
        ) : (
          <Detail selectedLocation={selectedLocation} />
        )}
      </div>
    </div>
  );
}

export default BottomSheet;
