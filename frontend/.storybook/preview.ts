import { createElement } from 'react';

import GlobalStyle from '../src/shared/styles/GlobalStyle';
import { colorToken } from '../src/shared/styles/tokens';

import type { Decorator } from '@storybook/react';
import type { Preview } from '@storybook/react-webpack5';

const withGlobalStyle: Decorator = (Story) => {
  return createElement(
    'div',
    null,
    createElement(GlobalStyle),
    createElement(Story),
  );
};

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
    backgrounds: {
      options: {
        bg1: { name: 'bg1', value: colorToken.bg[1] },
        bg2: { name: 'bg2', value: colorToken.bg[2] },
        dark: { name: 'Dark', value: '#333333' },
        light: { name: 'Light', value: '#FFFFFF' },
      },
    },
  },
  initialGlobals: {
    backgrounds: { value: 'bg1' },
  },
  decorators: [withGlobalStyle],
};

export default preview;
