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

import * as bottomSheet from './bottomSheet.styled';

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
