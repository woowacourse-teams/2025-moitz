import { css } from '@emotion/react';

export const wheelArea = () => css`
  display: flex;
  justify-content: space-around;
  height: 200px;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    height: 48px;
    background-color: rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    pointer-events: none;
  }
`;

export const wheelContainer = () => css`
  flex: 1;
  position: relative;
`;
