import { LocationRequestBody } from '@entities/types/LocationRequestBody';

const BASE_URL = process.env.API_BASE_URL;

const fetchLocations = async (requestBody: LocationRequestBody) => {
  const response = await fetch(`${BASE_URL}/locations/test`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  });

  if (!response.ok) {
    throw new Error('이동 경로를 찾을 수 없습니다.');
  }

  return response.json();
};

export default fetchLocations;
