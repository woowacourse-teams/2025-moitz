import React, { useRef, useState } from 'react';

import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import BottomSheetDetail from '../bottomSheetDetail/BottomSheetDetail';
import BottomSheetList from '../bottomSheetList/BottomSheetList';

import * as bottomSheet from './bottomSheet.styled';

interface BottomSheetViewProps {
  children: React.ReactNode;
  positionPercent: number;
  handleProps: React.HTMLAttributes<HTMLButtonElement>;
}

function BottomSheetView({
  children,
  positionPercent,
  handleProps,
}: BottomSheetViewProps) {
  return (
    <div
      css={[
        bottomSheet.wrapper(positionPercent),
        flex({ direction: 'column' }),
      ]}
    >
      <div css={[shadow.bottom_sheet, bottomSheet.container()]}>
        <div css={[bottomSheet.header()]}>
          <button
            css={[bottomSheet.handle()]}
            aria-label="시트 끌기 핸들"
            {...handleProps}
          />
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

// 유틸: 퍼센트 클램프
const clampPct = (p: number) => Math.max(0, Math.min(100, p));

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

  const onPointerDown = () => {};
  const onPointerMove = () => {};
  const onPointerUp = () => {};

  return (
    <BottomSheetView
      positionPercent={positionPercent}
      handleProps={{
        onPointerDown,
        onPointerMove,
        onPointerUp,
      }}
    >
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
