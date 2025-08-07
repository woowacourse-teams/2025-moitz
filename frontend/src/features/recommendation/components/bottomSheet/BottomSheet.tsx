import { useState } from 'react';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import { recommendedLocation } from '@shared/types/recommendedLocation';
import { startingLocation } from '@shared/types/startingLocation';

import Detail from '../detail/Detail';
import List from '../list/List';

import * as bottomSheet from './bottomSheet.styled';
type View = 'list' | 'detail';
interface BottomSheetProps {
  startingLocations: startingLocation[];
  recommendedLocations: recommendedLocation[];
}

function BottomSheet({
  startingLocations,
  recommendedLocations,
}: BottomSheetProps) {
  const [currentView, setCurrentView] = useState<View>('list');
  const [selectedLocation, setSelectedLocation] =
    useState<recommendedLocation | null>(null);

  const handleSpotClick = (spot: recommendedLocation) => {
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
