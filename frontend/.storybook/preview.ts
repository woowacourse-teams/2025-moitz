import React from 'react';

import GlobalStyle from '../src/shared/styles/GlobalStyle';

import type { Preview } from '@storybook/react-webpack5';

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
  decorators: [
    (Story) => {
      return React.createElement(
        React.Fragment,
        null,
        React.createElement(GlobalStyle),
        React.createElement(Story),
      );
    },
  ],
};

export default preview;
