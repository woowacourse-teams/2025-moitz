import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 2px;
  height: 10px;
  margin: 0 4px;
  background-color: ${colorToken.gray[7]};
`;
