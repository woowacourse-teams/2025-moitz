import fetchLocations from '@entities/apis/fetchLocations';
import { Location } from '@entities/types/Location';
import { useEffect, useState } from 'react';

interface LocationRequestBody {
  startingPlaceNames: string[];
  meetingTime: string;
  requirement: string;
}

const useLocations = (
  requestBody: LocationRequestBody,
): {
  data: Location[];
  loading: boolean;
  error: boolean;
} => {
  const [data, setData] = useState<Location[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const locations = await fetchLocations(requestBody);
        setData(locations);
      } catch (error) {
        setError(error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return { data, loading, error };
};

export default useLocations;
