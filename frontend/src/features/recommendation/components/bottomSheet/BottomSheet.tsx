import React, { useEffect, useRef, useState } from 'react';

import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import BottomSheetDetail from '../bottomSheetDetail/BottomSheetDetail';
import BottomSheetList from '../bottomSheetList/BottomSheetList';

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
  const containerRef = useRef<HTMLDivElement | null>(null);
  const contentRef = useRef<HTMLDivElement | null>(null);

  const [height, setHeight] = useState<number>(vh2px(50)); // 초기 50vh
  const startYRef = useRef<number | null>(null);
  const startHRef = useRef<number>(vh2px(50));

  // focus 가능 (a11y)
  useEffect(() => {
    containerRef.current?.setAttribute('tabindex', '-1');
  }, []);

  // ESC → 중간 단계(50vh)
  const onKeyDown: React.KeyboardEventHandler<HTMLDivElement> = (e) => {
    if (e.key === 'Escape') setHeight(vh2px(50));
  };

  // 드래그 시작 (header/handle 구역)
  const onPointerDown: React.PointerEventHandler<HTMLDivElement> = (e) => {
    const el = e.target as HTMLElement;
    const isHandleArea =
      !!el.closest('[data-handle]') || !!el.closest('[data-header]');
    if (!isHandleArea) return;

    // 내부 스크롤과 충돌 방지: 콘텐츠가 맨 위일 때만 아래로 당기는 제스처 허용
    if (contentRef.current && el.closest('[data-content]')) {
      if (contentRef.current.scrollTop > 0) return;
    }

    el.setPointerCapture?.(e.pointerId);
    startYRef.current = e.clientY;
    startHRef.current = height;
  };

  // 드래그 중 (실시간 높이 반영)
  const onPointerMove: React.PointerEventHandler<HTMLDivElement> = (e) => {
    if (startYRef.current == null) return;
    const dy = startYRef.current - e.clientY; // 위로 +, 아래로 -
    const next = startHRef.current + dy;

    const minPx = vh2px(SNAP_VH[0]);
    const maxPx = vh2px(SNAP_VH[SNAP_VH.length - 1]);
    const clamped = Math.min(Math.max(next, minPx), maxPx);
    setHeight(clamped);
  };

  // 드래그 종료 → 방향성 바이어스 + 스냅
  const DIRECTION_DEAD_ZONE_PX = 8; // 미세 떨림 무시
  const onPointerUp: React.PointerEventHandler<HTMLDivElement> = (e) => {
    if (startYRef.current == null) return;

    const deltaY = startYRef.current - e.clientY; // 위로 +, 아래로 -
    const pulledUp = deltaY > DIRECTION_DEAD_ZONE_PX;
    const pulledDown = deltaY < -DIRECTION_DEAD_ZONE_PX;

    setHeight(() => {
      if (pulledUp) return nextSnap(startHRef.current); // 살짝만 위로 → 다음 단계
      if (pulledDown) return prevSnap(startHRef.current); // 살짝만 아래로 → 이전 단계
      return nearestSnap(height); // 거의 안 움직였으면 가까운 단계
    });

    startYRef.current = null;
  };

  // 핸들 클릭 시 다음 단계로
  const onHandleClick: React.MouseEventHandler<HTMLButtonElement> = () => {
    setHeight((prev) => nextSnap(prev));
  };

  return (
    <div
      ref={containerRef}
      css={[
        flex({ direction: 'column' }),
        shadow.bottom_sheet,
        bottomSheet.container(),
      ]}
      style={{
        height, // 드래그/스냅으로 제어
        overflow: 'hidden',
        transition: startYRef.current == null ? 'height 160ms ease' : 'none',
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby="bs-title"
      onKeyDown={onKeyDown}
      onPointerDown={onPointerDown}
      onPointerMove={onPointerMove}
      onPointerUp={onPointerUp}
    >
      <div css={[bottomSheet.header()]} data-header>
        <button
          css={[bottomSheet.handle()]}
          data-handle
          type="button"
          aria-label="시트를 위아래로 드래그하거나 눌러 단계 변경"
          onClick={onHandleClick}
        />
      </div>

      <div
        ref={contentRef}
        data-content
        css={[
          flex({ direction: 'column', gap: 20 }),
          scroll,
          bottomSheet.content(),
        ]}
      >
        {/* 시맨틱 제목(시각 숨김) */}
        <h2 id="bs-title" css={bottomSheet.srOnly()}>
          추천 결과
        </h2>

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
      </div>
    </div>
  );
}

export default BottomSheet;

/* -------------------- 설정  -------------------- */
const SNAP_VH = [25, 50, 90] as const;
type SnapVH = (typeof SNAP_VH)[number];

const vh2px = (v: number) => Math.round((window.innerHeight * v) / 100);
const px2vh = (px: number) => (px / window.innerHeight) * 100;

function nearestSnap(pxHeight: number) {
  const cur = px2vh(pxHeight);
  let best: SnapVH = SNAP_VH[0];
  let diff = Math.abs(cur - best);
  for (let i = 1; i < SNAP_VH.length; i++) {
    const val: SnapVH = SNAP_VH[i];
    const d = Math.abs(cur - val);
    if (d < diff) {
      best = val;
      diff = d;
    }
  }
  return vh2px(best);
}

function snapIndexOf(pxHeight: number) {
  const cur = Math.round(px2vh(pxHeight));
  let idx = 0;
  let bestDiff = Infinity;
  for (let i = 0; i < SNAP_VH.length; i++) {
    const d = Math.abs(cur - SNAP_VH[i]);
    if (d < bestDiff) {
      bestDiff = d;
      idx = i;
    }
  }
  return idx;
}

function nextSnap(pxHeight: number) {
  const i = snapIndexOf(pxHeight);
  return vh2px(SNAP_VH[Math.min(i + 1, SNAP_VH.length - 1)]);
}

function prevSnap(pxHeight: number) {
  const i = snapIndexOf(pxHeight);
  return vh2px(SNAP_VH[Math.max(i - 1, 0)]);
}
