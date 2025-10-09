import { useEffect, useState } from 'react';

type useModalReturns = {
  isModalOpen: boolean;
  handleModalOpen: () => void;
  handleModalClose: () => void;
};

const useModal = (): useModalReturns => {
  const [isOpen, setIsOpen] = useState(false);

  const handleOpen = () => {
    setIsOpen(true);
  };

  const handleClose = () => {
    setIsOpen(false);
  };

  useEffect(() => {
    const handleEscKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        handleClose();
      }
    };

    if (isOpen) {
      window.addEventListener('keydown', handleEscKeyDown);
      document.body.style.overflow = 'hidden';
    }

    return () => {
      window.removeEventListener('keydown', handleEscKeyDown);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, handleClose]);

  return {
    isModalOpen: isOpen,
    handleModalOpen: handleOpen,
    handleModalClose: handleClose,
  };
};

export default useModal;
