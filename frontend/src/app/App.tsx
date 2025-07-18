import { ThemeProvider } from '@emotion/react';

import defaultTheme from '../shared/styles/defaultTheme';
import GlobalStyle from '../shared/styles/GlobalStyle';

export default function App() {
  return (
    <ThemeProvider theme={defaultTheme}>
      <GlobalStyle />
      <div css={(theme) => ({ color: theme.colors.gray[0] })}>some other text</div>
    </ThemeProvider>
  );
}
