import '@emotion/react';
import defaultTheme from './defaultTheme';

type ThemeType = typeof defaultTheme;

declare module '@emotion/react' {
  export interface Theme extends ThemeType {}
}
