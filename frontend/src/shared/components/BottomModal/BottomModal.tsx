/** @jsxImportSource @emotion/react */
import React from 'react';

import { flex, typography } from '@shared/styles/default.styled';

import BottomButton from '../bottomButton/BottomButton';

import * as bottomModal from './bottomModal.styled';

interface BottomModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  confirmText?: string;
  onConfirm?: () => void;
  showConfirmButton?: boolean;
}

function BottomModal({
  isOpen,
  onClose,
  title,
  children,
  confirmText = '완료',
  onConfirm,
  showConfirmButton = true,
}: BottomModalProps) {
  if (!isOpen) return null;

  return (
    <div css={bottomModal.overlay()} onClick={onClose}>
      <div css={bottomModal.modal()} onClick={(e) => e.stopPropagation()}>
        <div
          css={[
            flex({ justify: 'space-between', align: 'center' }),
            bottomModal.padding(),
          ]}
        >
          <span css={[typography.h2, bottomModal.header_title()]}>{title}</span>
          <button
            css={[
              flex({ justify: 'center', align: 'center' }),
              bottomModal.closeButton(),
              typography.h1,
            ]}
            onClick={onClose}
          >
            ✕
          </button>
        </div>

        <div css={bottomModal.padding()}>{children}</div>

        {showConfirmButton && (
          <div css={bottomModal.padding()}>
            <BottomButton
              type="button"
              text={confirmText}
              active={true}
              onClick={onConfirm}
            />
          </div>
        )}
      </div>
    </div>
  );
}

export default BottomModal;
