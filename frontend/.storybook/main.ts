import type { StorybookConfig } from '@storybook/react-webpack5';

const config: StorybookConfig = {
  stories: ['../src/**/*.stories.@(ts|tsx)', '../src/**/*.mdx'],
  addons: ['@storybook/addon-docs', '@storybook/addon-webpack5-compiler-babel'],
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  webpackFinal: async (config) => {
    // Babel 설정 추가
    config.module?.rules?.push({
      test: /\.(ts|tsx)$/,
      exclude: /node_modules/,
      use: {
        loader: 'babel-loader',
        options: {
          presets: [
            [
              '@babel/preset-react',
              { runtime: 'automatic', importSource: '@emotion/react' },
            ],
            [
              '@babel/preset-typescript',
              {
                onlyRemoveTypeImports: true,
                allowDeclareFields: true,
              },
            ],
            '@babel/preset-env',
          ],
          plugins: ['@emotion'],
        },
      },
    });

    // 확장자 처리 추가
    config.resolve?.extensions?.push('.ts', '.tsx');

    return config;
  },
};

export default config;
