import { useEffect, useState } from 'react';

import fetchLocations from '@entities/apis/fetchLocations';
import { Location } from '@entities/types/Location';
import { LocationRequestBody } from '@entities/types/LocationRequestBody';

type useLocationsReturn = {
  data: Location;
  isLoading: boolean;
  isError: boolean;
  errorMessage: string;
};

const initialData = {
  startingPlaces: [],
  recommendedLocations: [],
};

const useLocations = (requestBody: LocationRequestBody): useLocationsReturn => {
  const [data, setData] = useState<Location>(initialData);
  const [isLoading, setIsLoading] = useState(true);
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const locations = await fetchLocations(requestBody);
        setData(locations);
      } catch (error) {
        setIsError(true);
        setErrorMessage(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  return { data, isLoading, isError, errorMessage };
};

export default useLocations;
