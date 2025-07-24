import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const container = () => css`
  width: 100%;
  height: 100vh;
  background-color: ${colorToken.bg[1]};
  padding-bottom: 30px;
  overflow-y: auto;
`;

export const headerLogo = () => css`
  width: 100%;
  padding: 90px 0 50px;
`;
