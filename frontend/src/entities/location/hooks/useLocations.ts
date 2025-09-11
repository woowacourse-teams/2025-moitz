import { useState, useCallback } from 'react';

import { fetchRecommendationId } from '@entities/location/api/fetchRecommendationId';
import { fetchRecommendationResult } from '@entities/location/api/fetchRecommendationResult';
import { RecommendationRequestBody } from '@entities/location/api/types/RecommendationIdAPI';
import { Location } from '@entities/location/types/Location';

export type useLocationsReturn = {
  data: Location;
  isProgressLoading: boolean;
  isLoading: boolean;
  isError: boolean;
  errorMessage: string;
  getRecommendationId: (requestBody: RecommendationRequestBody) => Promise<string>;
  getRecommendationResult: (id: string) => Promise<Location>;
  getRecommendation: (requestBody: RecommendationRequestBody) => Promise<{ id: string; data: Location }>;
};

const initialData: Location = {
  startingPlaces: [],
  recommendedLocations: []
};

const useLocations = (): useLocationsReturn => {
  const [data, setData] = useState<Location>(initialData);
  const [isProgressLoading, setIsProgressLoading] = useState(false); // ProgressLoading 컴포넌트 렌더링 여부
  const [isLoading, setIsLoading] = useState(false); // 데이터 fetch에 따른 로딩 여부
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const getRecommendationId = useCallback(
    async (requestBody: RecommendationRequestBody) => {
      setIsLoading(true);
      setIsError(false);
      setErrorMessage('');

    try {
      return await fetchRecommendationId(requestBody);
    } catch (error) {
      setIsError(true);
      setErrorMessage(error instanceof Error ? error.message : String(error));
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const getRecommendationResult = useCallback(async (id: string) => {
    setIsLoading(true);
    setIsError(false);
    setErrorMessage('');

    try {
      const locations = await fetchRecommendationResult(id);
      setData(locations);
      return locations;
    } catch (error) {
      setIsError(true);
      setErrorMessage(error instanceof Error ? error.message : String(error));
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const getRecommendation = useCallback(async (requestBody: RecommendationRequestBody) => {
    setIsProgressLoading(true);
    setData(initialData);
    try {
      const newId = await getRecommendationId(requestBody);
      const newData = await getRecommendationResult(newId);

      await new Promise((resolve) => setTimeout(resolve, 600));

      return { id: newId, data: newData };
    } catch (error) {
      setIsError(true);
      setErrorMessage(error instanceof Error ? error.message : String(error));
      throw error;
    } finally {
      setIsProgressLoading(false);
    }
  }, []);

  return {
    data,
    isProgressLoading,
    isLoading,
    isError,
    errorMessage,
    getRecommendationId,
    getRecommendationResult,
    getRecommendation
  };
};

export default useLocations;
