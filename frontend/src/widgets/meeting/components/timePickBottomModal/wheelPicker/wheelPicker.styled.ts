import { css } from '@emotion/react';

export const wheelContainer = () => css`
  flex: 1;
  position: relative;
`;

export const wheelScroller = () => css`
  height: 200px;
  overflow-y: auto;
  scroll-behavior: smooth;

  &::-webkit-scrollbar {
    display: none;
  }
  scrollbar-width: none;
`;

export const wheelPadding = () => css`
  height: 76px;
`;

export const wheelItem = () => css`
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  transition: color 0.2s ease;
`;

export const wheelItemSelected = () => css`
  color: #000;
  font-weight: 600;
`;
