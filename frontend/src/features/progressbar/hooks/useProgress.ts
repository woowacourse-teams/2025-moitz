import { useCallback, useEffect, useRef, useState } from 'react';

const PROGRESS_MIN_VALUE = 0; // 프로그레스바 최소 상태 값 (0%)
const PROGRESS_MAX_VALUE = 100; // 프로그레스바 최대 상태 값 (100%)
const PROGRESS_COMPLETE_DURATION = 500; // 프로그레스바 애니메이션 완료 시간 (밀리초)

type UseProgressParams = {
  duration?: number;
  initialProgress?: number;
  targetProgress?: number;
};

type UseProgressReturns = {
  progress: number;
  complete: () => void;
};

export const useProgress = ({
  duration = 10000,
  initialProgress = PROGRESS_MIN_VALUE,
  targetProgress = PROGRESS_MAX_VALUE
}: UseProgressParams = {}): UseProgressReturns => {
  const [progress, setProgress] = useState(initialProgress);
  const animationRef = useRef<number>(null);

  const startTimeRef = useRef<number>(0);

  /**
   * 프로그레스바의 애니메이션을 처리하는 함수
   * @param duration - 애니메이션 지속 시간 (밀리초)
   * @param from - 시작 진행률
   * @param to - 목표 진행률
   */
  const animate = useCallback((duration: number, from: number, to: number) => {
    startTimeRef.current = performance.now();
    const startProgress = from;

    const animationFrame = (currentTime: number) => {
      setProgress((currentProgress) => {
        if (currentProgress === to) return currentProgress;

        const elapsed = currentTime - startTimeRef.current;

        if (elapsed >= duration) {
          return to;
        }

        const ratio = elapsed / duration;
        const easedProgress = startProgress + (to - startProgress) * (1 - Math.pow(1 - ratio, 3));
        const easedProgressNumber = Math.floor(easedProgress * 1000) / 1000;
        return easedProgressNumber;
      });

      animationRef.current = requestAnimationFrame(animationFrame);
    };

    animationRef.current = requestAnimationFrame(animationFrame);
  }, []);

  /**
   * 프로그레스바의 기본 진행 애니메이션을 처리하는 effect
   * 주의: 이미 PROGRESS_MAX_VALUE에 도달한 경우 애니메이션을 시작하지 않습니다.
   */
  useEffect(() => {
    if (duration <= 0) {
      throw new Error('duration must be greater than 0');
    }

    if (initialProgress < PROGRESS_MIN_VALUE || initialProgress > PROGRESS_MAX_VALUE) {
      throw new Error(`Initial progress must be between ${PROGRESS_MIN_VALUE} and ${PROGRESS_MAX_VALUE}`);
    }

    if (targetProgress < PROGRESS_MIN_VALUE || targetProgress > PROGRESS_MAX_VALUE) {
      throw new Error(`Target progress must be between ${PROGRESS_MIN_VALUE} and ${PROGRESS_MAX_VALUE}`);
    }

    if (progress !== PROGRESS_MAX_VALUE) {
      animate(duration, initialProgress, targetProgress);
    }

    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, []);

  /**
   * 프로그레스바를 즉시 완료 상태로 전환하는 함수
   * 현재 진행률에서 PROGRESS_MAX_VALUE까지 PROGRESS_COMPLETE_DURATION 동안 애니메이션을 실행합니다.
   *
   * 주의:
   * - 이미 PROGRESS_MAX_VALUE인 경우 아무 동작도 하지 않습니다.
   * - 실행 중인 기존 애니메이션은 취소됩니다.
   */
  const complete = useCallback(() => {
    setProgress((currentProgress) => {
      if (currentProgress === PROGRESS_MAX_VALUE) return currentProgress;

      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }

      animate(PROGRESS_COMPLETE_DURATION, currentProgress, PROGRESS_MAX_VALUE);
      return currentProgress;
    });
  }, [animate]);

  return {
    progress,
    complete
  };
};
