import { css } from '@emotion/react';

import { borderRadiusToken } from '@shared/styles/tokens';
import { colorToken } from '@shared/styles/tokens';

export const reason = () => css`
  padding: 10px;
  background-color: ${colorToken.bg[2]};
  border-radius: ${borderRadiusToken.input};
`;

export const reasonText = () => css`
  color: ${colorToken.gray[2]};
`;
