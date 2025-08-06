import { useEffect, useState } from 'react';

import fetchLocations from '@entities/apis/fetchLocations';
import { Location } from '@entities/types/Location';

type LocationRequestBody = {
  startingPlaceNames: string[];
  meetingTime: string;
  requirement: string;
};

type useLocationsReturn = {
  data: Location[];
  loading: boolean;
  error: boolean;
  errorMessage: string;
};

const useLocations = (requestBody: LocationRequestBody): useLocationsReturn => {
  const [data, setData] = useState<Location[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const locations = await fetchLocations(requestBody);
        setData(locations);
      } catch (error) {
        setError(true);
        setErrorMessage(error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return { data, loading, error, errorMessage };
};

export default useLocations;
