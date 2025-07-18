import { css } from '@emotion/react';

import { colors, borderRadius } from '../../styles/tokens';

export const base = () => css`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  border-radius: ${borderRadius.round};
  color: ${colors.gray[7]};
  background-color: ${colors.main[1]};
`;
