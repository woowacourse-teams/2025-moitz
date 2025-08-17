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

  const activePointerIdRef = useRef<number | null>(null);

  useSyncViewportHeight(viewportRef);

  /**
   * onPointerDown
   * - 드래그가 '시작'되는 순간에 단 한 번 호출됨
   *   여기서 '기준점'을 캡쳐해둔다
   **/
  const onPointerDown = (e: React.PointerEvent<HTMLDivElement>) => {
    // 이미 드래그 중이면 무시한다 (안전성)
    if (isDraggingRef.current) return;

    //  드래그를 시작한 '그 손가락'을 내 버튼에 묶어두고 캡처함
    // 매 이벤트마다 정말 그 손가락이 맞는지(ID) 확인해서,
    // 중간에 영역을 벗어나도/다른 손가락이 닿아도 드래그가 안 끊기게 함.
    e.currentTarget.setPointerCapture(e.pointerId);
    activePointerIdRef.current = e.pointerId;

    isDraggingRef.current = true;

    // 화면상의 현재 포인터 Y좌표(px)를 기록한다 (기준점 1)
    startYRef.current = e.clientY;
    // 드래그 시작 당시의 퍼센트 위치를 기록한다. (기준점 2)
    startPercentRef.current = positionPercent;
  };

  /**
   * onPointerMove
   * - 드래그 '중'에 호출되어 바텀시트 위치(%)를 실시간으로 갱신함
   *   쉽게 말하자면, 눌렀던 순간의 기준점에서,
   *   햔재 포인터가 Y축으로 얼마나 움직였는지(px)를 화면 높이로 나눠 퍼센트로 바꾸고,
   *   그만큼 시트 위치를 업데이트한다.
   */
  const onPointerMove = (e: React.PointerEvent<HTMLDivElement>) => {
    // 내 손가락만 반영
    if (activePointerIdRef.current !== e.pointerId) return;

    // // 드래그 중이 아닐 때 들어오는 move 이벤트는 무시 (안전성)
    if (!isDraggingRef.current) return;

    console.log(e.clientY, positionPercent);
    // 1) 드래그 이동량(px). 아래로 끌면 양수, 위로 끌면 음수
    const dragDistanceInPx = e.clientY - startYRef.current;

    // 2) 현재 보이는 뷰포트 높이(px). 0일 가능성에 대비해 최소 1로 가드
    const viewportHeightInPx = viewportRef.current || 1;

    // 3) 이동량을 0 ~ 100  퍼센트로 환산
    const dragDistanceInPercent = (dragDistanceInPx / viewportHeightInPx) * 100;

    // 4) 새 위치 퍼센트 계산
    //  - 시작 퍼센트에서 이동 퍼센트를 뺀다.
    //  - 위로 드래그하면 dragDistanceInPercent가 음수 → 결과적으로 위치 퍼센트가 증가(시트가 더 올라옴)
    const tentativePositionPercent =
      startPercentRef.current - dragDistanceInPercent;

    // 5) 상태 반영
    setPositionPercent(tentativePositionPercent);
  };

  /**
   * onPointerUp
   * - 드래그 종료만 처리
   */
  const onPointerUp = (e: React.PointerEvent<HTMLDivElement>) => {
    if (activePointerIdRef.current !== e.pointerId) return;
    activePointerIdRef.current = null; // 내 손가락(ID) 반납
  };

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
