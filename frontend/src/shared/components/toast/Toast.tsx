/** @jsxImportSource @emotion/react */
import { useEffect } from 'react';
import { createPortal } from 'react-dom';

import { typography } from '@shared/styles/default.styled';

import * as toast from './toast.styled';

interface ToastProps {
  message: string;
  isVisible: boolean;
  onClose: () => void;
}

function Toast({ message, isVisible, onClose }: ToastProps) {
  useEffect(() => {
    if (isVisible) {
      const timer = setTimeout(() => {
        onClose();
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [isVisible, onClose]);

  if (!isVisible) return null;

  return createPortal(
    <div css={toast.container}>
      <div css={[toast.content, typography.b1]}>{message}</div>
    </div>,
    document.body,
  );
}

export default Toast;
