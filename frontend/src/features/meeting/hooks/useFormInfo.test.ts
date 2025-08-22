import { renderHook, act } from '@testing-library/react';

import { useFormInfo } from './useFormInfo';

describe('useFormInfo 단위 테스트', () => {
  describe('출발지 입력 validation', () => {
    it('출발지를 입력시 정상적으로 서울 내의 올바른 지하철 역이름을 입력한다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        const validationResult =
          result.current.addDepartureWithValidation('강남역');
        expect(validationResult.isValid).toBe(true);
        expect(validationResult.message).toBe('');
      });

      expect(result.current.departureList).toHaveLength(1);
      expect(result.current.departureList).toContain('강남역');
    });

    it('출발지를 입력시 올바른 역 이름을 입력하지 않으면 에러를 반환한다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        const validationResult =
          result.current.addDepartureWithValidation('잘못된역이름');
        expect(validationResult.isValid).toBe(false);
        expect(validationResult.message).toBe(
          '서울 내의 올바른 지하철 역이름을 입력해주세요',
        );
      });
    });

    it('출발지를 입력시 서울내의 존재하지 않는 역이름을 입력하면 에러를 반환한다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        const validationResult =
          result.current.addDepartureWithValidation('부산역');
        expect(validationResult.isValid).toBe(false);
        expect(validationResult.message).toBe(
          '서울 내의 올바른 지하철 역이름을 입력해주세요',
        );
      });
    });
  });

  describe('출발지 중복 입력 validation', () => {
    it('정상적으로 출발지를 입력하여 출발지 목록에 추가된다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        result.current.addDepartureWithValidation('강변역');
        const validationResult =
          result.current.addDepartureWithValidation('강남역');
        expect(validationResult.isValid).toBe(true);
      });

      expect(result.current.departureList).toHaveLength(2);
      expect(result.current.departureList).toContain('강변역');
      expect(result.current.departureList).toContain('강남역');
    });

    it('출발지 입력시 이전에 입력한 동일한 출발지를 입력하면 에러를 반환한다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        result.current.addDepartureWithValidation('강남역');
      });

      act(() => {
        const validationResult =
          result.current.addDepartureWithValidation('강남역');
        expect(validationResult.isValid).toBe(false);
        expect(validationResult.message).toBe('이미 추가된 출발지예요');
      });

      expect(result.current.departureList).toHaveLength(1);
      expect(result.current.departureList).toContain('강남역');
    });
  });

  describe('출발지 입력 최대 개수 validation', () => {
    it('정상적으로 출발지 입력하여 출발지 목록에 추가된다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        result.current.addDepartureWithValidation('강남역');
        result.current.addDepartureWithValidation('역삼역');
        const validationResult =
          result.current.addDepartureWithValidation('선릉역');
        expect(validationResult.isValid).toBe(true);
        expect(validationResult.message).toBe('');
      });

      expect(result.current.departureList).toHaveLength(3);
      expect(result.current.departureList).toContain('강남역');
      expect(result.current.departureList).toContain('역삼역');
      expect(result.current.departureList).toContain('선릉역');
    });

    it('6개 이상의 출발지를 입력하면 에러를 반환한다', () => {
      const { result } = renderHook(() => useFormInfo());

      act(() => {
        result.current.addDepartureWithValidation('강남역');
        result.current.addDepartureWithValidation('역삼역');
        result.current.addDepartureWithValidation('선릉역');
        result.current.addDepartureWithValidation('삼성중앙역');
        result.current.addDepartureWithValidation('종각역');
        result.current.addDepartureWithValidation('종로3가역');
      });

      act(() => {
        const validationResult =
          result.current.addDepartureWithValidation('시청역');

        expect(validationResult.isValid).toBe(false);
        expect(validationResult.message).toBe(
          '출발지는 최대 6개까지 추가할 수 있어요',
        );
      });

      expect(result.current.departureList).toHaveLength(6);
    });
  });
});
