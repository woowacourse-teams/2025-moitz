import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const overlay = () => css`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
`;

export const modal = () => css`
  background: ${colorToken.gray[8]};
  border-radius: ${borderRadiusToken.global} ${borderRadiusToken.global} 0 0;
  width: 100%;
  max-height: 70vh;
  overflow: hidden;
`;

export const padding = () => css`
  padding: 20px;
`;

export const header_title = () => css`
  color: ${colorToken.gray[2]};
`;

export const closeButton = () => css`
  background: none;
  border: none;
  cursor: pointer;
  color: ${colorToken.gray[5]};
  padding: 0;
  width: 24px;
  height: 24px;
`;
