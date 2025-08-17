import React from 'react';

import { flex, scroll, shadow } from '@shared/styles/default.styled';

import * as bottomSheetView from './bottomSheetView.styled';

interface BottomSheetViewProps {
  children: React.ReactNode;
  positionPercent: number;
  handleProps: React.HTMLAttributes<HTMLDivElement>;
}

function BottomSheetView({
  children,
  positionPercent,
  handleProps,
}: BottomSheetViewProps) {
  return (
    <div
      css={[
        bottomSheetView.base(positionPercent),
        flex({ direction: 'column' }),
      ]}
    >
      <div css={[shadow.bottom_sheet, bottomSheetView.container()]}>
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
