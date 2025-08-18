import { renderHook, waitFor, act } from '@testing-library/react';
import { http, HttpResponse } from 'msw';

import { LocationsRequestBodyMock } from '@mocks/LocationsRequestBodyMock';
import { server } from '@mocks/server';

import useLocations from './useLocations';

const BASE_URL = process.env.API_BASE_URL;

describe('useLocations', () => {
  it('정상적으로 추천 장소를 받아온다', async () => {
    // when: 훅을 실행하면
    const { result } = renderHook(() => useLocations());
    result.current.trigger(LocationsRequestBodyMock);

    // then: 초기에는 로딩 중이어야 한다
    await act(async () => {
      result.current.trigger(LocationsRequestBodyMock);
    });

    // then: 데이터가 정상적으로 로드되어야 한다
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
      expect(result.current.isError).toBe(false);
      expect(result.current.data.recommendedLocations.length).toBeGreaterThan(
        0,
      );
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
    const { result } = renderHook(() => useLocations());
    result.current.trigger(LocationsRequestBodyMock);

    // then: 초기에는 로딩 중이어야 한다
    await act(async () => {
      result.current.trigger(LocationsRequestBodyMock);
    });

    // then: 요청 실패 후 error는 true, data는 비어 있어야 한다
    await waitFor(() => {
      expect(result.current.isLoading).toBe(false);
      expect(result.current.isError).toBe(true);
      expect(result.current.data).toEqual({
        startingPlaces: [],
        recommendedLocations: [],
      });
      expect(result.current.errorMessage).toContain(
        '이동 경로를 찾을 수 없습니다.',
      );
    });
  });
});
