import { useEffect, useState } from 'react';

type UseProgressProps = {
  duration?: number;
  initialProgress?: number;
  targetProgress?: number;
};

type UseProgressReturn = {
  progress: number;
  isComplete: boolean;
  complete: () => void;
};

export const useProgress = ({
  duration = 25000,
  initialProgress = 0,
  targetProgress = 90,
}: UseProgressProps = {}): UseProgressReturn => {
  const [progress, setProgress] = useState(initialProgress);
  const [isComplete, setIsComplete] = useState(false);

  useEffect(() => {
    const startTime = Date.now();
    let animationId: number;

    const animationFrame = () => {
      const currentTime = Date.now();
      const elapsed = currentTime - startTime;

      if (elapsed >= duration) {
        setProgress(targetProgress);
        return;
      }

      const ratio = elapsed / duration;
      const easedProgress = Math.min(
        targetProgress,
        targetProgress * (1 - Math.pow(1 - ratio, 3)),
      );

      setProgress(easedProgress);
      animationId = requestAnimationFrame(animationFrame);
    };

    animationId = requestAnimationFrame(animationFrame);

    return () => {
      setProgress(initialProgress);
      cancelAnimationFrame(animationId);
    };
  }, [duration, initialProgress, targetProgress]);

  const complete = () => {
    setProgress(100);
    setIsComplete(true);
  };

  return {
    progress,
    isComplete,
    complete,
  };
};
