import { css, keyframes } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

const slideUp = keyframes`
  0% {
    transform: translateY(100%);
    opacity: 0;
  }
  20% {
    transform: translateY(-10px);
    opacity: 1;
  }
  80% {
    transform: translateY(-10px);
    opacity: 1;
  }
  100% {
    transform: translateY(100%);
    opacity: 0;
  }
`;

export const container = css`
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  width: fit-content;
  max-width: calc(100% - 60px);
  min-width: calc(100% - 100px);
`;

export const content = css`
  background-color: ${colorToken.gray[7]};
  color: ${colorToken.gray[4]};
  padding: 12px 20px;
  border-radius: ${borderRadiusToken.global};
  animation: ${slideUp} 3s ease-in-out forwards;
  text-align: center;
`;
