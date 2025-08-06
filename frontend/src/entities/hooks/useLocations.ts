import { useEffect, useState } from 'react';

import fetchLocations from '@entities/apis/fetchLocations';
import { Location } from '@entities/types/Location';

type useLocationsReturn = {
  data: Location[];
  isLoading: boolean;
  isError: boolean;
  errorMessage: string;
};

export type LocationRequestBody = {
  startingPlaceNames: string[];
  meetingTime: string;
  requirement: string;
};

const useLocations = (requestBody: LocationRequestBody): useLocationsReturn => {
  const [data, setData] = useState<Location[]>([]);
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
