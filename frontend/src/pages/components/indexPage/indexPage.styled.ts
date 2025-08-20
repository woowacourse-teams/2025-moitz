import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 100%;
  height: 100%;
  background-color: ${colorToken.bg[1]};
  padding-bottom: 30px;
`;

export const headerLogo = () => css`
  width: 100%;
  padding: 70px 0;
`;
