import React, { useRef, useState } from 'react';

import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import BottomSheetDetail from '../bottomSheetDetail/BottomSheetDetail';
import BottomSheetList from '../bottomSheetList/BottomSheetList';
import BottomSheetView from '../bottomSheetView/bottomSheetView';

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

  const isDraggingRef = useRef(false);
  const startYRef = useRef(0);
  const startPercentRef = useRef(positionPercent);

  /**
   * onPointerDown
   * - 드래그가 '시작'되는 수간에 단 한 번 호출됨
   * 여기서 '기준점'을 갭쳐해 둔다
   **/
  const onPointerDown = (e: React.PointerEvent<HTMLButtonElement>) => {
    console.log('down');
    // 이미 드래그 중이면 무시한다 (안전성)
    if (isDraggingRef.current) return;

    isDraggingRef.current = true;

    // 화면상의 현재 포인터 Y좌표(px)를 기록한다
    startYRef.current = e.clientY;
    // 드래그 시작 당시의 퍼센트 위치를 기록한다.
    startPercentRef.current = positionPercent;
  };
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
