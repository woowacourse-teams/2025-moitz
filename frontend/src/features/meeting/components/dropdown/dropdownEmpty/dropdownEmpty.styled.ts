import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 12px;
  text-align: left;
  color: ${colorToken.gray[5]};
`;
