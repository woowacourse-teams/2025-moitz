import { useState, useCallback } from 'react';

import { fetchRecommendationId } from '@entities/location/api/fetchRecommendationId';
import { fetchRecommendationResult } from '@entities/location/api/fetchRecommendationResult';
import { RecommendationRequestBody } from '@entities/location/api/types/RecommendationIdAPI';
import { Location } from '@entities/location/types/Location';

export type useLocationsReturn = {
  data: Location;
  isLoading: boolean;
  isError: boolean;
  errorMessage: string;
  getRecommendationId: (
    requestBody: RecommendationRequestBody,
  ) => Promise<string>;
  getRecommendationResult: (id: string) => Promise<void>;
};

const initialData: Location = {
  startingPlaces: [],
  recommendedLocations: [],
};

const useLocations = (): useLocationsReturn => {
  const [data, setData] = useState<Location>(initialData);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const getRecommendationId = useCallback(
    async (requestBody: RecommendationRequestBody) => {
      setIsLoading(true);
      setIsError(false);
      setErrorMessage('');

      try {
        const id = await fetchRecommendationId(requestBody);
        setIsLoading(false);
        return id;
      } catch (error) {
        setIsError(true);
        setErrorMessage(error instanceof Error ? error.message : String(error));
        setIsLoading(false);
        throw error;
      }
    },
    [],
  );

  const getRecommendationResult = useCallback(async (id: string) => {
    setIsLoading(true);
    setIsError(false);
    setErrorMessage('');

    try {
      const locations = await fetchRecommendationResult(id);
      setData(locations);
      setIsLoading(false);
    } catch (error) {
      setIsError(true);
      setErrorMessage(error instanceof Error ? error.message : String(error));
      setIsLoading(false);
      throw error;
    }
  }, []);

  return {
    data,
    isLoading,
    isError,
    errorMessage,
    getRecommendationId,
    getRecommendationResult,
  };
};

export default useLocations;
