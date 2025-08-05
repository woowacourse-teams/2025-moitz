import { renderHook, waitFor } from '@testing-library/react';
import { http, HttpResponse } from 'msw';

import { server } from '@mocks/server';

import useLocations from './useLocations';

const BASE_URL = process.env.API_BASE_URL;

const requestBody = {
  startingPlaceNames: ['강변역', '신촌역'],
  meetingTime: '14:00',
  requirement: '노래방',
};

describe('useLocations', () => {
  it('정상적으로 추천 장소를 받아온다', async () => {
    // when: 훅을 실행하면
    const { result } = renderHook(() => useLocations(requestBody));

    // then: 초기에는 로딩 중이어야 한다
    expect(result.current.loading).toBe(true);

    // then: 데이터가 정상적으로 로드되어야 한다
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe(false);
      expect(result.current.data.length).toBeGreaterThan(0);
    });
  });

  it('API 요청이 실패하면 error 상태가 true가 된다', async () => {
    // given: 서버가 500 에러를 응답하도록 설정
    server.use(
      http.post(`${BASE_URL}/locations`, () =>
        HttpResponse.json(
          { message: 'Internal Server Error' },
          { status: 500 },
        ),
      ),
    );

    // when: 훅을 실행하면
    const { result } = renderHook(() => useLocations(requestBody));

    // then: 초기에는 로딩 중이어야 한다
    expect(result.current.loading).toBe(true);

    // then: 요청 실패 후 error는 true, data는 비어 있어야 한다
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe(true);
      expect(result.current.data.length).toBe(0);
    });
  });
});
