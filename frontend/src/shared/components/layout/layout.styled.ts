import { css } from '@emotion/react';

import { colorToken, layout } from '@shared/styles/tokens';
import '@shared/styles/tokens.css';

export const container = () => css`
  display: flex;
  width: 100%;
  min-height: 100dvh;
  background-color: ${colorToken.main[4]};
`;

export const content = () => css`
  width: 100%;
  min-height: 100dvh;
  overflow-x: hidden;
  background-color: ${colorToken.gray[8]};

  @media (min-width: ${layout.media_maxWidth}) {
    flex: 0 0 ${layout.maxWidth};
  }
`;

export const side = () => css`
  display: none;

  @media (min-width: ${layout.media_maxWidth}) {
    display: initial;
    width: 100%;
  }
`;
