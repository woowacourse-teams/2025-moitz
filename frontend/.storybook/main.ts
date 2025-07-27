import path from 'path';
import { fileURLToPath } from 'url';
import type { StorybookConfig } from '@storybook/react-webpack5';

// __dirname 대체
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-webpack5-compiler-swc', '@storybook/addon-docs'],
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  webpackFinal: async (config) => {
    if (config.resolve) {
      config.resolve.alias = {
        ...config.resolve.alias,
        '@app': path.resolve(__dirname, '../src/app'),
        '@pages': path.resolve(__dirname, '../src/pages'),
        '@widgets': path.resolve(__dirname, '../src/widgets'),
        '@features': path.resolve(__dirname, '../src/features'),
        '@shared': path.resolve(__dirname, '../src/shared'),
        '@shared/components': path.resolve(
          __dirname,
          '../src/shared/components',
        ),
        '@shared/styles': path.resolve(__dirname, '../src/shared/styles'),
        '@shared/types': path.resolve(__dirname, '../src/shared/types'),
        '@icons': path.resolve(__dirname, '../assets/icon'),
      };
    }
    return config;
  },
};
export default config;
