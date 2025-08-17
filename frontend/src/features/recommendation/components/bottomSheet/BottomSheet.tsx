import React, { useEffect, useRef, useState } from 'react';

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
  const [positionPercent, setPositionPercent] = useState(60);

  const isDraggingRef = useRef(false);
  const startYRef = useRef(0);
  const startPercentRef = useRef(positionPercent);
  const viewportRef = useRef<number>(getViewportHeight());

  useSyncViewportHeight(viewportRef);

  /**
   * onPointerDown
   * - 드래그가 '시작'되는 순간에 단 한 번 호출됨
   *   여기서 '기준점'을 캡쳐해 둔다
   **/
  const onPointerDown = (e: React.PointerEvent<HTMLButtonElement>) => {
    // 이미 드래그 중이면 무시한다 (안전성)
    if (isDraggingRef.current) return;

    isDraggingRef.current = true;

    // 화면상의 현재 포인터 Y좌표(px)를 기록한다 (기준점 1)
    startYRef.current = e.clientY;
    // 드래그 시작 당시의 퍼센트 위치를 기록한다. (기준점 2)
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

/**
 * useSyncViewportHeight
 * - '현재 보이는' 뷰포트 높이(px)로 동기화함 (viewportRef)
 *
 * 참고: `passive: true`
 * - 이 리스너에서는 `event.preventDefault()`를 호출하지 않겠다는 약속
 * - 브라우저가 "취소 안 한다"는 확신을 가지게 되어,
 *   스크롤/터치/리사이즈 같은 기본 동작을 JS 실행을 기다리지 않고 바로 처리 → 체감 성능↑
 */
export function useSyncViewportHeight(viewportRef: { current: number }) {
  useEffect(() => {
    const update = () => {
      viewportRef.current = getViewportHeight();
    };

    // 마운트 직후 1회 보정
    update();

    // 1) 창 크기 변경
    window.addEventListener('resize', update, { passive: true });

    // 2) 기기 회전 (모바일 가로/세로 전환)
    window.addEventListener('orientationchange', update, { passive: true });

    // 3) 실제 가시 영역 변화 (iOS 주소창 수축/확장 등)
    const { visualViewport } = window;
    if (visualViewport) {
      visualViewport.addEventListener('resize', update, { passive: true });
    }

    // 5) 언마운트 시 정리 (메모리 누수/중복 리스너 방지)
    return () => {
      window.removeEventListener('resize', update);
      window.removeEventListener('orientationchange', update);
      if (visualViewport) visualViewport.removeEventListener('resize', update);
    };
  }, [viewportRef]);
}

/**
 * getViewportHeight
 * - 현재 화면에서 '실제로 보이는' 뷰포트 높이를 px 단위로 반환함
 *
 * - 모바일(iOS Safari 등)에서는 주소창 수축/확장으로 `window.innerHeight`가
 *   실제 보이는 높이와 달라질 수 있어 `window.visualViewport.height`를 우선 사용합니다.
 * - `visualViewport`를 지원하지 않는 환경에서는 `window.innerHeight`를 사용합니다.
 **/
const getViewportHeight = () => {
  if (typeof window === 'undefined') return 0;

  const visualViewport = window.visualViewport;
  if (visualViewport && typeof visualViewport.height === 'number') {
    return visualViewport.height;
  }

  return window.innerHeight;
};
