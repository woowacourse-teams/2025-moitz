import { useEffect, useState } from 'react';

import { typography } from '@shared/styles/default.styled';

import * as progressText from './progressText.styled';

interface ProgressTextProps {
  text: string[];
}

function ProgressText({ text }: ProgressTextProps) {
  const [textIndex, setTextIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setTextIndex((prev) => (prev + 1) % text.length);
    }, 600);

    return () => clearInterval(interval);
  }, []);

  return (
    <div css={[typography.b2, progressText.text()]}>{text[textIndex]}</div>
  );
}

export default ProgressText;
