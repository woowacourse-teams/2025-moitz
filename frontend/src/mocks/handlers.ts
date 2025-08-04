import dotenv from 'dotenv';
import { http, HttpResponse } from 'msw';

dotenv.config({ path: '.env' });

const BASE_URL = process.env.API_BASE_URL;

export const handlers = [
  http.post(`${BASE_URL}/locations`, async () => {
    return HttpResponse.json([
      {
        id: 1,
        index: 1,
        x: 127.0,
        y: 37.5,
        name: '왕십리역',
        avgMinutes: 18,
        isBest: true,
        description: '중간 위치',
        reason: '교통 편리',
        places: [],
        routes: [],
      },
    ]);
  }),
];
