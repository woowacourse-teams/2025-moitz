import { getApiBaseUrl } from '@config/env';

const BASE_URL = getApiBaseUrl();

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

export type RequestConfig = {
  endpoint: string;
  method?: HttpMethod;
  body?: unknown;
  headers?: Record<string, string>;
};

class ApiError extends Error {
  constructor(
    public status: number,
    message?: string,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export const createApiRequest = async <T>({
  endpoint,
  method = 'GET',
  body,
  headers: customHeaders = {},
}: RequestConfig): Promise<T> => {
  const url = `${BASE_URL}${endpoint}`;

  const headers = {
    'Content-Type': 'application/json',
    ...customHeaders,
  };

  try {
    const response = await fetch(url, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });

    if (!response.ok) {
      let errorMessage = `API 요청 실패: ${response.status} ${response.statusText}`;

      try {
        const errorData = await response.json();
        if (errorData?.message && typeof errorData.message === 'string') {
          errorMessage = errorData.message;
        }
      } catch {
        // JSON 파싱 실패 무시
      }

      throw new ApiError(response.status, errorMessage);
    }

    const data = await response.json();
    return data as T;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }

    const errorMessage =
      error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다';
    throw new ApiError(500, errorMessage);
  }
};

export const apiClient = {
  get: <T>(
    endpoint: string,
    config?: Omit<RequestConfig, 'method' | 'endpoint'>,
  ) => createApiRequest<T>({ ...config, endpoint, method: 'GET' }),

  post: <T>(
    endpoint: string,
    body?: unknown,
    config?: Omit<RequestConfig, 'method' | 'endpoint' | 'body'>,
  ) => createApiRequest<T>({ ...config, endpoint, method: 'POST', body }),

  put: <T>(
    endpoint: string,
    body?: unknown,
    config?: Omit<RequestConfig, 'method' | 'endpoint' | 'body'>,
  ) => createApiRequest<T>({ ...config, endpoint, method: 'PUT', body }),

  delete: <T>(
    endpoint: string,
    config?: Omit<RequestConfig, 'method' | 'endpoint'>,
  ) => createApiRequest<T>({ ...config, endpoint, method: 'DELETE' }),
};
