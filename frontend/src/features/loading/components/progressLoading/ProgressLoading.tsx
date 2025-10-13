import { useEffect } from 'react';

import BaseLoading from '@features/loading/components/baseLoading/BaseLoading';
import { LOADING_TEXT } from '@features/loading/constants/loadingText';
import Progressbar from '@features/progressbar/components/progressbar/Progressbar';
import { useProgress } from '@features/progressbar/hooks/useProgress';

import ProgressText from '../progressText/ProgressText';

interface ProgressLoadingProps {
  isReadyToComplete: boolean;
}

function ProgressLoading({ isReadyToComplete }: ProgressLoadingProps) {
  const { progress, complete } = useProgress({
    initialProgress: 0,
    targetProgress: 90,
  });

  useEffect(() => {
    if (isReadyToComplete) {
      complete();
    }
  }, [isReadyToComplete, complete]);

  return (
    <BaseLoading>
      <Progressbar progress={progress} />
      <ProgressText text={LOADING_TEXT} />
    </BaseLoading>
  );
}

export default ProgressLoading;
