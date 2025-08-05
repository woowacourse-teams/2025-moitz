// eslint-disable-next-line @typescript-eslint/no-require-imports
const path = require('path');

/** @type {import('@storybook/react-webpack5').StorybookConfig} */
module.exports = {
  stories: ['../src/**/*.stories.@(ts|tsx)', '../src/**/*.mdx'],

  addons: [
    getAbsolutePath('@storybook/addon-docs'),
    getAbsolutePath('@storybook/addon-webpack5-compiler-babel'),
  ],

  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },

  webpackFinal: async (config) => {
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
              { onlyRemoveTypeImports: true, allowDeclareFields: true },
            ],
            '@babel/preset-env',
          ],
          plugins: ['@emotion'],
        },
      },
    });

    config.resolve?.extensions?.push('.ts', '.tsx');

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
        '@mocks': path.resolve(__dirname, '../src/mocks'),
      };
    }

    return config;
  },
};

/**
 * 주어진 패키지 이름의 package.json 절대 경로를 기반으로 루트 디렉토리 경로 반환
 * @param {string} pkg 패키지 이름 (e.g. '@storybook/addon-docs')
 * @returns {string} 절대 경로
 */
function getAbsolutePath(pkg) {
  return path.dirname(require.resolve(path.join(pkg, 'package.json')));
}
