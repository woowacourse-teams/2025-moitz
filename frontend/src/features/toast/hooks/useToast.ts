import { useState, useCallback, useEffect } from 'react';

export function useToast() {
  const [isVisible, setIsVisible] = useState(false);
  const [message, setMessage] = useState('');

  const showToast = useCallback((newMessage: string) => {
    setMessage(newMessage);
    setIsVisible(true);
  }, []);

  const hideToast = useCallback(() => {
    setIsVisible(false);
  }, []);

  useEffect(() => {
    if (isVisible) {
      const timer = setTimeout(() => {
        hideToast();
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [isVisible, hideToast]);

  return {
    isVisible,
    message,
    showToast,
  };
}
