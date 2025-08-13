import { css } from '@emotion/react';
import { Decorator } from '@storybook/react-webpack5';

export const withContainer: Decorator = (Story) => (
  <div
    css={css`
      width: 400px;
      margin: 0 auto;
    `}
  >
    <Story />
  </div>
);
