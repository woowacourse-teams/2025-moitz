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
    '^@app/(.*)$': '<rootDir>/src/app/$1',
    '^@pages/(.*)$': '<rootDir>/src/pages/$1',
    '^@widgets/(.*)$': '<rootDir>/src/widgets/$1',
    '^@features/(.*)$': '<rootDir>/src/features/$1',
    '^@entities/(.*)$': '<rootDir>/src/entities/$1',
    '^@shared/(.*)$': '<rootDir>/src/shared/$1',
    '^@shared/components/(.*)$': '<rootDir>/src/shared/components/$1',
    '^@shared/styles/(.*)$': '<rootDir>/src/shared/styles/$1',
    '^@shared/types/(.*)$': '<rootDir>/src/shared/types/$1',
    '^@icons/(.*)$': '<rootDir>/assets/icon/$1',
    '^@mocks/(.*)$': '<rootDir>/src/mocks/$1',
    '^@config/(.*)$': '<rootDir>/src/config/$1',
    '^@sb/(.*)$': '<rootDir>/.storybook/$1',
  },
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
};

export default config;
