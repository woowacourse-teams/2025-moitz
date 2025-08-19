import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  position: absolute;
  top: 110%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); // TODO : default shadow 적용
  z-index: 1000;
  background-color: ${colorToken.gray[8]};
  border: 1px solid ${colorToken.gray[7]};
  border-radius: ${borderRadiusToken[10]};
`;
