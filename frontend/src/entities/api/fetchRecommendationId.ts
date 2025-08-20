import {
  RecommendationRequestBody,
  RecommendationResponse,
} from '@entities/api/types/RecommendationIdAPITypes';

import { apiClient } from '@shared/api/apiClient';

export const fetchRecommendationId = async (
  requestBody: RecommendationRequestBody,
): Promise<number> => {
  // TODO : string 으로 변경
  const response = await apiClient.post<RecommendationResponse>(
    '/recommendations/test', // TODO : 추후 실제 api 로 수정
    requestBody,
  );
  return response.id;
};
