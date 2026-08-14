import React, { useEffect } from 'react';
import { useParams } from 'react-router';

import useVoteModal from '@widgets/voting/hooks/useVoteModal';

import MapVoteButton from '@shared/components/mapVoteButton/MapVoteButton';
import { flex, scroll, shadow } from '@shared/styles/default.styled';

import * as bottomSheetView from './bottomSheetView.styled';

interface BottomSheetViewProps {
  children: React.ReactNode;
  positionPercent: number;
  handleProps: React.HTMLAttributes<HTMLDivElement>;
  isAnimating: boolean;
  onContainerTransitionEnd: React.TransitionEventHandler<HTMLDivElement>;
  containerRef: React.RefObject<HTMLDivElement>;
}

function BottomSheetView({
  children,
  positionPercent,
  handleProps,
  isAnimating,
  onContainerTransitionEnd,
  containerRef,
}: BottomSheetViewProps) {
  const { id } = useParams<{ id: string }>();
  const { openVoteModal } = useVoteModal();

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.style.transform = `translate3d(0, ${100 - positionPercent}%, 0)`;
    }
  }, [positionPercent, containerRef]);

  const handleVoteButtonClick = async () => {
    if (id) {
      await openVoteModal(id);
    }
  };

  return (
    <div css={[bottomSheetView.base()]} ref={containerRef}>
      <div
        css={[
          flex({ direction: 'column' }),
          shadow.bottom_sheet,
          bottomSheetView.container(),
          isAnimating && bottomSheetView.animate(),
        ]}
        // CSS transition이 끝났을 때 호출되는 이벤트 핸들러
        onTransitionEnd={onContainerTransitionEnd}
      >
        <div css={bottomSheetView.voteButtonWrapper()}>
          <MapVoteButton onClick={handleVoteButtonClick} />
        </div>

        <div css={[bottomSheetView.header()]} {...handleProps}>
          <span css={[bottomSheetView.handle()]} aria-hidden />
        </div>

        <div
          css={[
            flex({ direction: 'column', gap: 20 }),
            scroll,
            bottomSheetView.content(),
          ]}
        >
          {children}
        </div>
      </div>
    </div>
  );
}

export default BottomSheetView;
