import { useState, useCallback } from 'react';

import fetchLocations from '@entities/apis/fetchLocations';
import { Location } from '@entities/types/Location';
import { LocationRequestBody } from '@entities/types/LocationRequestBody';

type useLocationsReturn = {
  data: Location;
  isLoading: boolean;
  isError: boolean;
  errorMessage: string;
  trigger: (requestBody: LocationRequestBody) => Promise<void>;
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

  const trigger = useCallback(async (requestBody: LocationRequestBody) => {
    setIsLoading(true);
    setIsError(false);
    setErrorMessage('');

    try {
      const locations = await fetchLocations(requestBody);
      setData(locations);
    } catch (error) {
      setIsError(true);
      setErrorMessage(error instanceof Error ? error.message : String(error));
    } finally {
      setIsLoading(false);
    }
  }, []);

  return { data, isLoading, isError, errorMessage, trigger };
};

export default useLocations;
