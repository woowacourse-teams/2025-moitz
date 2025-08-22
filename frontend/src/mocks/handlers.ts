import dotenv from 'dotenv';
import { http, HttpResponse } from 'msw';

import { LocationsMock } from './LocationsMock';

dotenv.config({ path: '.env' });

const BASE_URL =
  process.env.NODE_ENV === 'production'
    ? process.env.PROD_API_BASE_URL
    : process.env.DEV_API_BASE_URL;

export const handlers = [
  http.post(`${BASE_URL}/recommendations`, async () => {
    return HttpResponse.json('test-recommendation-id', { status: 200 });
  }),
  http.get(`${BASE_URL}/recommendations/:id`, async () => {
    return HttpResponse.json(LocationsMock, { status: 200 });
  }),
];
