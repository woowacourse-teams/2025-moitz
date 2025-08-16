import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const nameList = () => css`
  color: ${colorToken.gray[4]};
`;

export const dot = () => css`
  width: 3px;
  min-width: 3px;
  height: 3px;
  border-radius: ${borderRadiusToken[100]};
  background-color: ${colorToken.main[1]};
`;
