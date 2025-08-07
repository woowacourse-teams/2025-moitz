import { createPortal } from 'react-dom';

import { flex, typography } from '@shared/styles/default.styled';

import IconToast from '@icons/icon-toast.svg';

import * as toast from './toast.styled';

interface ToastProps {
  message: string;
  isVisible: boolean;
}

function Toast({ message, isVisible }: ToastProps) {
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
