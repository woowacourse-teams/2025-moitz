import dotenv from 'dotenv';
import { http, HttpResponse } from 'msw';

import LocationsMock from './LocationsMock';

dotenv.config({ path: '.env' });

const BASE_URL = process.env.API_BASE_URL;

export const handlers = [
  http.post(`${BASE_URL}/locations`, async () => {
    return HttpResponse.json(LocationsMock);
  }),
];
