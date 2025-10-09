import React from 'react';

import { flex, grid_padding } from '@shared/styles/default.styled';

import IconCancel from '@icons/icon-cancel-gray.svg';

import * as modal from './modal.styled';

interface ModalProps {
  onClose: () => void;
  children: React.ReactNode;
}

function Modal({ onClose, children }: ModalProps) {
  const handleBackdropClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div
      css={[
        flex({ direction: 'column', justify: 'center', align: 'center' }),
        modal.backdrop(),
        grid_padding,
      ]}
      onClick={handleBackdropClick}
    >
      <div css={[flex({ direction: 'column', gap: 20 }), modal.base()]}>
        <div css={flex({ justify: 'flex-end' })}>
          <button type="button" onClick={onClose}>
            <img src={IconCancel} alt="취소 버튼"></img>
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default Modal;
