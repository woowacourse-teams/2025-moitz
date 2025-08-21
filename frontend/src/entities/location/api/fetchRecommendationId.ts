import {
  RecommendationRequestBody,
  RecommendationResponse,
} from '@entities/location/api/types/RecommendationIdAPITypes';

import { apiClient } from '@shared/api/apiClient';

export const fetchRecommendationId = async (
  requestBody: RecommendationRequestBody,
): Promise<string> => {
  const response = await apiClient.post<RecommendationResponse>(
    '/recommendations',
    requestBody,
  );

  return response.id;
};
