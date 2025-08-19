import React from 'react';

type Props = {
  children: React.ReactNode;
  fallbackRender?: (args: { reset: () => void }) => React.ReactNode; // 에러 시 그릴 UI를 함수로 받음
  onError?: (error: Error, info: React.ErrorInfo) => void; // 로깅/모니터링 훅
  onReset?: () => void; // reset 함수를 호출했을 때 실행되는 훅
};

type State = { hasError: boolean };

export class ErrorBoundary extends React.Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(): State {
    // 렌더 중 예외가 발생하면 에러 모드로 전환
    return { hasError: true };
  }

  componentDidCatch(error: Error, info: React.ErrorInfo) {
    this.props.onError?.(error, info);
  }

  private reset = () => {
    this.props.onReset?.();
    this.setState({ hasError: false });
  };

  render() {
    if (this.state.hasError) {
      return this.props.fallbackRender
        ? this.props.fallbackRender({ reset: this.reset })
        : null; // fallbackRender 미지정 시 아무것도 렌더하지 않음
    }
    return this.props.children;
  }
}
