import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import Modal from './Modal';

describe('Modal', () => {
  const mockOnClose = jest.fn();
  const testContent = '모달 내용';
  const user = userEvent.setup();

  beforeEach(() => {
    mockOnClose.mockClear();
  });

  const renderModal = () => {
    return render(
      <Modal onClose={mockOnClose}>
        <div>{testContent}</div>
      </Modal>,
    );
  };

  describe('모달 닫기', () => {
    it('취소 버튼을 클릭하면 모달이 닫힌다', async () => {
      // given
      renderModal();
      const cancelButton = screen.getByRole('button', {
        name: '취소 버튼',
      });

      // when
      await user.click(cancelButton);

      // then
      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('backdrop을 클릭하면 모달이 닫힌다', async () => {
      // given
      renderModal();
      const backdrop = screen.getByRole('dialog');

      // when
      await user.click(backdrop);

      // then
      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('backdrop에서 모달 내부를 클릭하면 모달이 닫히지 않는다', async () => {
      // given
      renderModal();
      const modalContent = screen.getByText(testContent);

      // when
      await user.click(modalContent);

      // then
      expect(mockOnClose).not.toHaveBeenCalled();
    });
  });
});
