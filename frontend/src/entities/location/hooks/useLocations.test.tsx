import { renderHook, waitFor, act } from '@testing-library/react';
import { http, HttpResponse } from 'msw';

import { LocationsRequestBodyMock } from '@mocks/LocationsRequestBodyMock';
import { server } from '@mocks/server';

import useLocations from './useLocations';

const BASE_URL =
  process.env.NODE_ENV === 'production'
    ? process.env.PROD_API_BASE_URL
    : process.env.DEV_API_BASE_URL;

describe('useLocations', () => {
  describe('getRecommendationId', () => {
    it('정상적으로 추천 ID를 받아온다', async () => {
      // when: 훅을 실행하면
      const { result } = renderHook(() => useLocations());

      // then: 초기에는 로딩 중이어야 한다
      await act(async () => {
        await result.current.getRecommendationId(LocationsRequestBodyMock);
      });

      // then: 데이터가 정상적으로 로드되어야 한다
      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isError).toBe(false);
      });
    });

    it('ID 요청이 실패하면 error 상태가 true가 된다', async () => {
      // given: 서버가 500 에러를 응답하도록 설정
      server.use(
        http.post(`${BASE_URL}/recommendations`, () =>
          HttpResponse.json(
            { message: 'Internal Server Error' },
            { status: 500 },
          ),
        ),
      );

      // when: 훅을 실행하면
      const { result } = renderHook(() => useLocations());

      // then: 요청이 실패하면 에러가 발생해야 한다
      await act(async () => {
        await expect(
          result.current.getRecommendationId(LocationsRequestBodyMock),
        ).rejects.toBeTruthy();
      });

      await waitFor(() => {
        expect(result.current.isError).toBe(true);
        expect(result.current.errorMessage).toBeTruthy();
      });
    });
  });

  describe('getRecommendationResult', () => {
    it('정상적으로 추천 결과를 받아온다', async () => {
      // when: 훅을 실행하면
      const { result } = renderHook(() => useLocations());
      const id = await result.current.getRecommendationId(
        LocationsRequestBodyMock,
      );

      // then: 초기에는 로딩 중이어야 한다
      await act(async () => {
        await result.current.getRecommendationResult(id);
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

    it('결과 요청이 실패하면 error 상태가 true가 된다', async () => {
      // given: 서버가 500 에러를 응답하도록 설정
      server.use(
        http.get(`${BASE_URL}/recommendations/JF768D13`, () =>
          HttpResponse.json(
            { message: 'Internal Server Error' },
            { status: 500 },
          ),
        ),
      );

      // when: 훅을 실행하면
      const { result } = renderHook(() => useLocations());

      // then: 요청이 실패하면 에러가 발생해야 한다
      await act(async () => {
        await expect(
          result.current.getRecommendationResult('JF768D13'),
        ).rejects.toBeTruthy();
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isError).toBe(true);
        expect(result.current.data).toEqual({
          startingPlaces: [],
          recommendedLocations: [],
        });
        expect(result.current.errorMessage).toBeTruthy();
      });
    });
  });
});
