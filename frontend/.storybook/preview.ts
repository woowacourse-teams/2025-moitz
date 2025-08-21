import { createElement } from 'react';
import { MemoryRouter } from 'react-router';

import GlobalStyle from '@shared/styles/GlobalStyle';
import { colorToken } from '@shared/styles/tokens';

import type { Decorator, StoryContext } from '@storybook/react';
import type { Preview } from '@storybook/react-webpack5';

// 글로벌 스타일 적용
const withGlobalStyle: Decorator = (Story) => {
  return createElement(
    'div',
    null,
    createElement(GlobalStyle),
    createElement(Story),
  );
};

// 라우팅 적용
const withMemoryRouter: Decorator = (Story, context: StoryContext) => {
  const path = context.parameters?.pathname ?? '/';

  return createElement(
    MemoryRouter,
    { initialEntries: [path] },
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
  decorators: [withMemoryRouter, withGlobalStyle],
};

export default preview;
