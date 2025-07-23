/** @jsxImportSource @emotion/react */
import { typography } from '@shared/styles/default.styled';

import * as textarea from './textarea.styled';

interface TextareaProps {
  placeholder: string;
}

function Textarea({ placeholder }: TextareaProps) {
  return (
    <textarea
      css={[textarea.base(), typography.b1]}
      placeholder={placeholder}
    ></textarea>
  );
}

export default Textarea;
