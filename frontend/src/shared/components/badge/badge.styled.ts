import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 3px 10px;
  border-radius: ${borderRadiusToken.global};
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.orange[2]};
`;
