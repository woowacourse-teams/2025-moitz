import { renderHook, waitFor } from '@testing-library/react';

import useLocations from './useLocations';

const requestBody = {
  startingPlaceNames: ['강변역', '신촌역'],
  meetingTime: '14:00',
  requirement: '노래방',
};

describe('useLocations', () => {
  it('정상적으로 추천 장소를 받아온다', async () => {
    // given: 올바른 요청 본문이 주어졌고
    const { result } = renderHook(() => useLocations(requestBody));

    // when: API 요청이 완료되면
    await waitFor(() => {
      // then: 로딩은 false, 에러는 false, 데이터는 존재해야 한다
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe(false);
      expect(result.current.data.length).toBeGreaterThan(0);
    });
  });
});
