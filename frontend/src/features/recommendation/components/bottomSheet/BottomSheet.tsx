import React, { useState } from 'react';

import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import BottomSheetDetail from '../bottomSheetDetail/BottomSheetDetail';
import BottomSheetList from '../bottomSheetList/BottomSheetList';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetViewProps {
  children: React.ReactNode;
  positionPercent: number;
}

function BottomSheetView({ children, positionPercent }: BottomSheetViewProps) {
  return (
    <div
      css={[
        bottomSheet.wrapper(positionPercent),
        flex({ direction: 'column' }),
      ]}
    >
      <div css={[shadow.bottom_sheet, bottomSheet.container()]}>
        <div css={[bottomSheet.header()]}>
          <button css={[bottomSheet.handle()]} aria-label="시트 끌기 핸들" />
        </div>

        <div
          css={[
            flex({ direction: 'column', gap: 20 }),
            scroll,
            bottomSheet.content(),
          ]}
        >
          {children}
        </div>
      </div>
    </div>
  );
}

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
  const [positionPercent, setPositionPercent] = useState(50);

  return (
    <BottomSheetView positionPercent={positionPercent}>
      {currentView === 'list' && (
        <BottomSheetList
          startingPlaces={startingLocations}
          recommendedLocations={recommendedLocations}
          onSpotClick={handleSpotClick}
        />
      )}
      {currentView === 'detail' && (
        <BottomSheetDetail
          startingPlaces={startingLocations}
          selectedLocation={selectedLocation}
        />
      )}
    </BottomSheetView>
  );
}

export default BottomSheet;
