import BaseLoading from '@features/loading/components/baseLoading/BaseLoading';
import { LOADING_TEXT } from '@features/loading/constant/loadingText';
import Progressbar from '@features/progressbar/components/progressbar/Progressbar';
import { useProgress } from '@features/progressbar/hooks/useProgress';

import ProgressText from '../progressText/ProgressText';

function ProgressLoading() {
  const { progress } = useProgress();

  return (
    <BaseLoading>
      <Progressbar progress={progress} />
      <ProgressText text={LOADING_TEXT} />
    </BaseLoading>
  );
}

export default ProgressLoading;
