import '@emotion/react';
import { CSSObject, SerializedStyles } from '@emotion/react';

declare module 'react' {
  interface Attributes {
    css?: CSSObject | SerializedStyles | SerializedStyles[];
  }
}
