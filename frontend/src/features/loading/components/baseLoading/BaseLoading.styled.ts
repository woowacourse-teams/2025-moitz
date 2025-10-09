import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const container = () => css`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: ${colorToken.blur};
  backdrop-filter: blur(4px);
  z-index: 9999;
`;
