import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3px 10px;
  border-radius: ${borderRadiusToken.global};
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.orange[2]};
`;
