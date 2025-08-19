import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 5px 10px;
  border-radius: ${borderRadiusToken.global};
  color: ${colorToken.gray[1]};
  background-color: ${colorToken.gray[8]};
`;
