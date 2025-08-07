import { useEffect, useState } from 'react';

import BaseLoading from '@features/loading/components/baseLoading/BaseLoading';
import { LOADING_TEXT } from '@features/loading/constant/loadingText';
import Progressbar from '@features/progressbar/components/progressbar/Progressbar';
import { useProgress } from '@features/progressbar/hooks/useProgress';

import { typography } from '@shared/styles/default.styled';

import * as loading from './progressLoading.styled';

interface ProgressLoadingProps {
  isLoading?: boolean;
  onLoadingComplete?: () => void;
}

function ProgressLoading({
  isLoading = true,
  onLoadingComplete,
}: ProgressLoadingProps) {
  const { progress, isComplete, complete } = useProgress();
  const [textIndex, setTextIndex] = useState(0);

  useEffect(() => {
    if (!isLoading && !isComplete) {
      complete();
    }
  }, [isLoading, isComplete]);

  useEffect(() => {
    if (isComplete && onLoadingComplete) {
      onLoadingComplete();
    }
  }, [isComplete, onLoadingComplete]);

  useEffect(() => {
    if (!isLoading || isComplete) return;

    const interval = setInterval(() => {
      setTextIndex((prev) => (prev + 1) % LOADING_TEXT.length);
    }, 600);

    return () => clearInterval(interval);
  }, [isLoading, isComplete]);

  return (
    <BaseLoading>
      <Progressbar progress={progress} />
      <div css={[typography.b2, loading.text()]}>{LOADING_TEXT[textIndex]}</div>
    </BaseLoading>
  );
}

export default ProgressLoading;
