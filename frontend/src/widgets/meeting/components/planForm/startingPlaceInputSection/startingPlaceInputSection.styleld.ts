import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const inputWrapper = () => css`
  position: relative;
`;

export const dropdown = () => css`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: ${colorToken.gray[8]};
  border: 1px solid ${colorToken.gray[6]};
  border-radius: ${borderRadiusToken.input};
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
`;

export const dropdownItem = (isLast: boolean) => css`
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: ${isLast ? 'none' : '1px solid #f1f3f4'};

  &:hover {
    background-color: #f8f9fa;
  }
`;
