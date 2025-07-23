import { css } from '@emotion/react';

import { colorToken } from '../../styles/tokens';

export const container = () => css`
  display: flex;
  width: 100%;
  height: 100vh;
  background-color: ${colorToken.main[4]};
`;

export const content = () => css`
  flex: 0 0 400px;
  height: 100%;
  overflow-y: hidden;
  background-color: ${colorToken.gray[8]};
`;

export const side = () => css`
  width: 100%;
`;
