/** @jsxImportSource @emotion/react */

import { useEffect } from 'react';
import { createPortal } from 'react-dom';

import { flex, typography } from '@shared/styles/default.styled';

import IconToast from '@icons/icon-toast.svg';

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
    <div css={toast.container()}>
      <div
        css={[
          toast.content(),
          flex({ justify: 'center', align: 'center', gap: 10 }),
        ]}
      >
        <img src={IconToast} alt="icon-toast" />
        <div css={[toast.text(), typography.b1]}>{message}</div>
      </div>
    </div>,
    document.body,
  );
}

export default Toast;
