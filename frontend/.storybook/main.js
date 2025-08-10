// eslint-disable-next-line @typescript-eslint/no-require-imports
const path = require('path');

/** @type {import('@storybook/react-webpack5').StorybookConfig} */
module.exports = {
  /**
   * stories 설정
   * - directory: 스토리 파일이 있는 폴더 경로
   * - titlePrefix: 자동 생성될 title의 상위 카테고리
   * - files: 스토리 파일 패턴
   * 이렇게 지정하면 title을 생략해도, 폴더 구조 기반으로 title이 자동 생성됨
   */
  stories: [
    { directory: '../src/features', titlePrefix: 'features', files: '**/*.stories.@(ts|tsx)'},
    { directory: '../src/entities', titlePrefix: 'entities', files: '**/*.stories.@(ts|tsx)' },
    { directory: '../src/widgets', titlePrefix: 'widgets', files: '**/*.stories.@(ts|tsx)' },
    { directory: '../src/pages', titlePrefix: 'pages', files: '**/*.stories.@(ts|tsx)' },
    { directory: '../src/shared', titlePrefix: 'shared', files: '**/*.stories.@(ts|tsx)' },
  ],

  addons: [
    getAbsolutePath('@storybook/addon-docs'),
    getAbsolutePath('@storybook/addon-webpack5-compiler-babel'),
  ],

  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },

  webpackFinal: async (config) => {
    // Emotion + TypeScript + React를 위한 Babel 로더 추가
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

    // .ts, .tsx 확장자 지원 추가
    config.resolve?.extensions?.push('.ts', '.tsx');

    // 절대 경로 alias 설정
    if (config.resolve) {
      config.resolve.alias = {
        ...config.resolve.alias,
        '@app': path.resolve(__dirname, '../src/app'),
        '@pages': path.resolve(__dirname, '../src/pages'),
        '@widgets': path.resolve(__dirname, '../src/widgets'),
        '@features': path.resolve(__dirname, '../src/features'),
        '@entities': path.resolve(__dirname, '../src/entities'),
        '@shared': path.resolve(__dirname, '../src/shared'),
        '@shared/components': path.resolve(__dirname, '../src/shared/components'),
        '@shared/styles': path.resolve(__dirname, '../src/shared/styles'),
        '@shared/types': path.resolve(__dirname, '../src/shared/types'),
        '@icons': path.resolve(__dirname, '../assets/icon'),
        '@mocks': path.resolve(__dirname, '../src/mocks'),
        '@config': path.resolve(__dirname, '../src/config'),
      };
    }

    return config;
  },
};

/**
 * 지정한 패키지 이름의 루트 디렉토리 경로 반환
 * Storybook addon 경로를 절대경로로 변환할 때 사용
 */
function getAbsolutePath(pkg) {
  return path.dirname(require.resolve(path.join(pkg, 'package.json')));
}
