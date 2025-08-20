import {
  RecommendationRequestBody,
  RecommendationResponse,
} from '@entities/location/api/types/RecommendationIdAPITypes';

import { apiClient } from '@shared/api/apiClient';

export const fetchRecommendationId = async (
  requestBody: RecommendationRequestBody,
): Promise<string> => {
  const response = await apiClient.post<RecommendationResponse>(
    '/recommendations/test', // TODO : 추후 실제 api 로 수정
    requestBody,
  );

  const id = response.id.toString(); // TODO : 추후 실제 타입을 위해 문자열로 변경함.
  return id;
};
