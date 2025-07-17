import { ThemeProvider } from '@emotion/react';
import { css } from '@emotion/react';

import defaultTheme from './shared/styles/defaultTheme';

export default function App() {
  return (
    <ThemeProvider theme={defaultTheme}>
      <div css={(theme) => ({ color: theme.colors.primary })}>some other text</div>
    </ThemeProvider>
  );
}
