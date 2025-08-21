import '@emotion/react';
import defaultTheme from './defaultTheme';

type ThemeType = typeof defaultTheme;

declare module '@emotion/react' {
  // eslint-disable-next-line @typescript-eslint/no-empty-object-type
  export interface Theme extends ThemeType {}
}
