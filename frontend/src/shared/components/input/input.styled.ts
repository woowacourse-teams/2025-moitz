import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  width: 100%;
  height: 100%;
  padding: 12px 10px;
  border-radius: ${borderRadiusToken.input};
  color: ${colorToken.gray[3]};
  background-color: ${colorToken.gray[8]};
  border: none;

  &:focus {
    box-shadow: 0 0 0 2px ${colorToken.main[1]};
    outline: none;
  }

  &::placeholder {
    color: ${colorToken.gray[5]};
  }
`;
