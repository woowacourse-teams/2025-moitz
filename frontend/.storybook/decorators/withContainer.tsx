import { css } from '@emotion/react';
import { Decorator } from '@storybook/react-webpack5';

import { layout } from '@shared/styles/tokens';

export const withContainer: Decorator = (Story) => (
  <div
    css={css`
      width: ${layout.maxWidth};
      margin: 0 auto;
    `}
  >
    <Story />
  </div>
);
