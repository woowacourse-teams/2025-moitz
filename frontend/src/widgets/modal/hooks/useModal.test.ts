import { act, renderHook } from '@testing-library/react';

import useModal from './useModal';

describe('useModal', () => {
  it('초기 상태는 닫혀있다', () => {
    // given
    const { result } = renderHook(() => useModal());

    // when
    const initialState = result.current.isModalOpen;

    // then
    expect(initialState).toBe(false);
  });

  it('handleModalOpen을 호출하면 모달이 열린다', () => {
    // given
    const { result } = renderHook(() => useModal());

    // when
    act(() => {
      result.current.handleModalOpen();
    });

    // then
    expect(result.current.isModalOpen).toBe(true);
  });

  it('handleModalClose를 호출하면 모달이 닫힌다', () => {
    // given
    const { result } = renderHook(() => useModal());
    act(() => {
      result.current.handleModalOpen();
    });

    // when
    act(() => {
      result.current.handleModalClose();
    });

    // then
    expect(result.current.isModalOpen).toBe(false);
  });

  it('ESC 키를 누르면 모달이 닫힌다', () => {
    // given
    const { result } = renderHook(() => useModal());
    act(() => {
      result.current.handleModalOpen();
    });
    expect(result.current.isModalOpen).toBe(true);

    // when
    act(() => {
      const escapeEvent = new KeyboardEvent('keydown', { key: 'Escape' });
      window.dispatchEvent(escapeEvent);
    });

    // then
    expect(result.current.isModalOpen).toBe(false);
  });
});
