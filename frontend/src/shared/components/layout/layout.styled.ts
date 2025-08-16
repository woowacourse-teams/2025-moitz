import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

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

  @media (min-width: 400px) {
    flex: 0 0 400px;
  }
`;

export const side = () => css`
  display: none;

  @media (min-width: 400px) {
    display: initial;
    width: 100%;
  }
`;
