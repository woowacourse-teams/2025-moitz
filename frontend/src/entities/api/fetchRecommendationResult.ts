import { LocationResponse } from '@entities/api/types/RecommendationResultAPITypes';
import { Location } from '@entities/types/Location';

import { apiClient } from '@shared/api/apiClient';

export const fetchRecommendationResult = async (
  id: string,
): Promise<Location> => {
  const data = await apiClient.get<LocationResponse>(
    `/recommendations/${id}/test`, // TODO : 추후 실제 api 로 수정
  );
  const transformedData: Location = {
    startingPlaces: data.startingPlaces,
    recommendedLocations: data.locations,
  };
  return transformedData;
};
