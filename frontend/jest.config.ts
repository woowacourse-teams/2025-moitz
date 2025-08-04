import type { Config } from 'jest';

const config: Config = {
  preset: 'ts-jest/presets/default-esm',
  testEnvironment: 'jsdom',
  testEnvironmentOptions: {
    customExportConditions: [''],
  },
  extensionsToTreatAsEsm: ['.ts', '.tsx'],
  transform: {
    '^.+\\.(ts|tsx)$': ['ts-jest', { useESM: true }],
  },
  setupFiles: ['<rootDir>/src/jest.polyfill.ts'],
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.ts'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|less|scss)$': 'identity-obj-proxy',
  },
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
};

export default config;
