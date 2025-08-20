import { useState, useCallback } from 'react';

import { fetchRecommendationId } from '@entities/location/api/fetchRecommendationId';
import { fetchRecommendationResult } from '@entities/location/api/fetchRecommendationResult';
import { RecommendationRequestBody } from '@entities/location/api/types/RecommendationIdAPITypes';
import { Location } from '@entities/location/types/Location';

export type useLocationsReturn = {
  data: Location;
  isLoading: boolean;
  isError: boolean;
  errorMessage: string;
  trigger: (requestBody: RecommendationRequestBody) => Promise<void>;
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

  const trigger = useCallback(
    async (requestBody: RecommendationRequestBody) => {
      setIsLoading(true);
      setIsError(false);
      setErrorMessage('');

      try {
        const id = await fetchRecommendationId(requestBody);
        const locations = await fetchRecommendationResult(id.toString());
        setData(locations);
      } catch (error) {
        setIsError(true);
        setErrorMessage(error instanceof Error ? error.message : String(error));
      } finally {
        setIsLoading(false);
      }
    },
    [],
  );

  return { data, isLoading, isError, errorMessage, trigger };
};

export default useLocations;
