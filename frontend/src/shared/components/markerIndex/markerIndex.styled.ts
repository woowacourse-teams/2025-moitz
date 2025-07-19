import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  border-radius: ${borderRadiusToken.round};
  color: ${colorToken.gray[7]};
  background-color: ${colorToken.main[1]};
`;
