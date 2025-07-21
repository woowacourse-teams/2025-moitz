import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: ${borderRadiusToken.round};
  background-color: ${colorToken.gray[8]};
`;
