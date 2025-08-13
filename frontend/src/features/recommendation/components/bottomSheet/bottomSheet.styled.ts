import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const container = () => css`
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) translateZ(0);

  width: 100%;
  max-width: 400px;
  min-height: 25vh;
  max-height: 90vh;

  padding: 0 20px;
  background-color: ${colorToken.gray[8]};
  border-top-left-radius: ${borderRadiusToken.input};
  border-top-right-radius: ${borderRadiusToken.input};
  box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  will-change: height;

  outline: none;

  @media (prefers-reduced-motion: reduce) {
    transition: none !important;
  }
`;

export const header = () => css`
  padding: 10px 0 16px;
  cursor: grab;
  touch-action: none;

  &:active {
    cursor: grabbing;
  }

  overscroll-behavior: contain;
`;

export const handle = () => css`
  width: 40px;
  height: 4px;
  background-color: ${colorToken.gray[7]};
  border-radius: 2px;
  margin: 8px auto 0;
  display: block;
  pointer-events: auto;

  &:focus-visible {
    outline: 2px solid ${colorToken.main?.[2] ?? '#2563eb'};
    outline-offset: 2px;
    border-radius: 4px;
  }
`;

export const content = () => css`
  padding-bottom: 20px;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
  flex: 1 1 auto;
`;

export const srOnly = () => css`
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0 0 0 0) !important;
  white-space: nowrap !important;
  border: 0 !important;
`;
