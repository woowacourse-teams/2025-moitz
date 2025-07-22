import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  padding: 5px 10px;
  border-radius: ${borderRadiusToken.global};
  color: ${colorToken.gray[1]};
  background-color: ${colorToken.gray[8]};
`;

export const dot = () => css`
  width: 8px;
  min-width: 8px;
  height: 8px;
  border-radius: ${borderRadiusToken.round};
  background-color: ${colorToken.main[1]};
`;
