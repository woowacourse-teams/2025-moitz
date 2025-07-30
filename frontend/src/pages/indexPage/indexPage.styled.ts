import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const container = () => css`
  width: 100%;
  height: 100vh;
  background-color: ${colorToken.bg[1]};
  padding-bottom: 30px;
`;

export const scroll = () => css`
  overflow-y: auto;
  -ms-overflow-style: none; /* IE, Edge */
  scrollbar-width: none; /* Firefox */
  &::-webkit-scrollbar {
    display: none; /* Chrome, Safari */
  }
`;

export const headerLogo = () => css`
  width: 100%;
  padding: 70px 0;
`;
