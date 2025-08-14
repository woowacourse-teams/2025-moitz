import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';

import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import BottomSheetDetail from '../bottomSheetDetail/BottomSheetDetail';
import BottomSheetList from '../bottomSheetList/BottomSheetList';

import * as bottomSheet from './bottomSheet.styled';

/** ===============================
 *  Container (export default)
 *  — 비즈니스 뷰(List/Detail) 조립
 * =============================== */
interface BottomSheetProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  currentView: View;
  selectedLocation: RecommendedLocation | null;
  handleSpotClick: (spot: RecommendedLocation) => void;
}

export default function BottomSheet({
  startingLocations,
  recommendedLocations,
  currentView,
  selectedLocation,
  handleSpotClick,
}: BottomSheetProps) {
  const ctrl = useBottomSheetController();

  return (
    <BottomSheetView
      height={ctrl.height}
      isDragging={ctrl.isDragging}
      containerRef={ctrl.containerRef}
      contentRef={ctrl.contentRef}
      onKeyDown={ctrl.onKeyDown}
      onPointerDown={ctrl.onPointerDown}
      onPointerMove={ctrl.onPointerMove}
      onPointerUp={ctrl.onPointerUp}
      onHandleClick={ctrl.onHandleClick}
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

/** ===============================
 *  View (internal)
 *  — DOM/스타일/ARIA만 담당하는 순수 뷰
 * =============================== */
function BottomSheetView(props: {
  height: number;
  isDragging: boolean;
  containerRef: React.RefObject<HTMLDivElement>;
  contentRef: React.RefObject<HTMLDivElement>;
  onKeyDown: React.KeyboardEventHandler<HTMLDivElement>;
  onPointerDown: React.PointerEventHandler<HTMLDivElement>;
  onPointerMove: React.PointerEventHandler<HTMLDivElement>;
  onPointerUp: React.PointerEventHandler<HTMLDivElement>;
  onHandleClick: React.MouseEventHandler<HTMLButtonElement>;
  titleId?: string;
  headerSlot?: React.ReactNode;
  children: React.ReactNode;
}) {
  const {
    height,
    isDragging,
    containerRef,
    contentRef,
    onKeyDown,
    onPointerDown,
    onPointerMove,
    onPointerUp,
    onHandleClick,
    titleId = 'bs-title',
    headerSlot,
    children,
  } = props;

  return (
    <div
      ref={containerRef}
      css={[
        flex({ direction: 'column' }),
        shadow.bottom_sheet,
        bottomSheet.container(),
      ]}
      style={{
        height,
        overflow: 'hidden',
        transition: isDragging ? 'none' : 'height 160ms ease',
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby={titleId}
      onKeyDown={onKeyDown}
      onPointerDown={onPointerDown}
      onPointerMove={onPointerMove}
      onPointerUp={onPointerUp}
    >
      <div css={[bottomSheet.header()]} data-header>
        {headerSlot ?? (
          <button
            css={[bottomSheet.handle()]}
            data-handle
            type="button"
            aria-label="시트를 위아래로 드래그하거나 눌러 단계 변경"
            onClick={onHandleClick}
          />
        )}
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
        <h2 id={titleId} css={bottomSheet.srOnly()}>
          추천 결과
        </h2>
        {children}
      </div>
    </div>
  );
}

/** ===============================
 *  Config & Utils (internal)
 *  — 스냅 계산과 단위 변환은 이 파일 내부에서만 사용
 * =============================== */
const SNAP_VH = [25, 50, 90] as const;
type SnapVH = (typeof SNAP_VH)[number];

const vh2px = (
  v: number,
  win: Window | undefined = typeof window !== 'undefined' ? window : undefined,
) => Math.round(((win?.innerHeight ?? 0) * v) / 100);
const px2vh = (
  px: number,
  win: Window | undefined = typeof window !== 'undefined' ? window : undefined,
) => (px / (win?.innerHeight ?? 1)) * 100;

function nearestSnapPx(pxHeight: number, win?: Window) {
  const cur = px2vh(pxHeight, win);
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
  return vh2px(best, win);
}

function snapIndexOf(pxHeight: number, win?: Window) {
  const cur = Math.round(px2vh(pxHeight, win));
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

function nextSnapPx(pxHeight: number, win?: Window) {
  const i = snapIndexOf(pxHeight, win);
  return vh2px(SNAP_VH[Math.min(i + 1, SNAP_VH.length - 1)], win);
}

function prevSnapPx(pxHeight: number, win?: Window) {
  const i = snapIndexOf(pxHeight, win);
  return vh2px(SNAP_VH[Math.max(i - 1, 0)], win);
}

/** ===============================
 *  Controller Hook (internal)
 *  — 포인터/키보드/리사이즈 등 로직 전담
 * =============================== */
function useBottomSheetController() {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const contentRef = useRef<HTMLDivElement | null>(null);

  const initial = useMemo(
    () => (typeof window === 'undefined' ? 0 : vh2px(50)),
    [],
  );
  const [height, setHeight] = useState<number>(initial);
  const [isDragging, setIsDragging] = useState(false);

  const startYRef = useRef<number | null>(null);
  const startHRef = useRef<number>(initial);

  // a11y: focus 진입 허용
  useEffect(() => {
    containerRef.current?.setAttribute('tabindex', '-1');
  }, []);

  // 리사이즈 시 현재 스냅 유지한 채 재계산
  useEffect(() => {
    if (typeof window === 'undefined') return;
    const onResize = () => setHeight((prev) => nearestSnapPx(prev));
    window.addEventListener('resize', onResize);
    return () => window.removeEventListener('resize', onResize);
  }, []);

  const onKeyDown: React.KeyboardEventHandler<HTMLDivElement> = (e) => {
    if (e.key === 'Escape') setHeight(vh2px(50));
  };

  const isHandleOrHeader = (el: HTMLElement) =>
    !!el.closest('[data-handle]') || !!el.closest('[data-header]');

  const DIRECTION_DEAD_ZONE_PX = 8;

  const onPointerDown: React.PointerEventHandler<HTMLDivElement> = (e) => {
    const el = e.target as HTMLElement;
    if (!isHandleOrHeader(el)) return;

    if (contentRef.current && el.closest('[data-content]')) {
      if (contentRef.current.scrollTop > 0) return; // 내부 스크롤 우선
    }

    el.setPointerCapture?.(e.pointerId);
    startYRef.current = e.clientY;
    startHRef.current = height;
    setIsDragging(true);
  };

  const onPointerMove: React.PointerEventHandler<HTMLDivElement> = (e) => {
    if (startYRef.current == null) return;
    if (typeof window === 'undefined') return;

    const dy = startYRef.current - e.clientY; // 위로 +, 아래로 -
    const next = startHRef.current + dy;

    const minPx = vh2px(SNAP_VH[0]);
    const maxPx = vh2px(SNAP_VH[SNAP_VH.length - 1]);
    const clamped = Math.min(Math.max(next, minPx), maxPx);
    setHeight(clamped);
  };

  const onPointerUp: React.PointerEventHandler<HTMLDivElement> = (e) => {
    if (startYRef.current == null) return;

    const deltaY = startYRef.current - e.clientY; // 위로 +, 아래로 -
    const pulledUp = deltaY > DIRECTION_DEAD_ZONE_PX;
    const pulledDown = deltaY < -DIRECTION_DEAD_ZONE_PX;

    setHeight((prev) => {
      if (pulledUp) return nextSnapPx(startHRef.current);
      if (pulledDown) return prevSnapPx(startHRef.current);
      return nearestSnapPx(prev);
    });

    startYRef.current = null;
    setIsDragging(false);
  };

  const onHandleClick: React.MouseEventHandler<HTMLButtonElement> =
    useCallback(() => {
      setHeight((prev) => nextSnapPx(prev));
    }, []);

  return {
    // state
    height,
    isDragging,
    // refs
    containerRef,
    contentRef,
    // handlers
    onKeyDown,
    onPointerDown,
    onPointerMove,
    onPointerUp,
    onHandleClick,
  } as const;
}
