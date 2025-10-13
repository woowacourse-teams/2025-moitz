/**
 * 환경별 API Base URL을 반환하는 유틸리티 함수
 */
export const getApiBaseUrl = (): string => {
  return process.env.NODE_ENV === 'production'
    ? process.env.PROD_API_BASE_URL || ''
    : process.env.DEV_API_BASE_URL || '';
};

/**
 * 환경 설정 관련 상수들
 */
export const ENV_CONFIG = {
  API_BASE_URL: getApiBaseUrl(),
  IS_PRODUCTION: process.env.NODE_ENV === 'production',
  IS_DEVELOPMENT: process.env.NODE_ENV === 'development',
} as const;
