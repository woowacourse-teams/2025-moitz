import { css, keyframes } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

const slideDown = () => keyframes`
  0% {
    transform: translateY(-100%);
    opacity: 0;
  }
  20% {
    transform: translateY(10px);
    opacity: 1;
  }
  80% {
    transform: translateY(10px);
    opacity: 1;
  }
  100% {
    transform: translateY(-100%);
    opacity: 0;
  }
`;

export const container = () => css`
  width: fit-content;
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
`;

export const content = () => css`
  min-width: calc(100%);
  padding: 10px 12px;
  text-align: center;
  background-color: ${colorToken.gray[7]};
  border-radius: ${borderRadiusToken.round};
  animation: ${slideDown()} 3s ease-in-out forwards;
`;

export const text = () => css`
  color: ${colorToken.gray[3]};
`;
