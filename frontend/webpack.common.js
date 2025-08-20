import path from 'node:path';
import { fileURLToPath } from 'node:url';

import HtmlWebpackPlugin from 'html-webpack-plugin';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// dev/prod에서 주입한 envVars를 받아 index.html에 templateParameters로 전달
export default function common(envVars = {}) {
  return {
    entry: './main.tsx',
    output: {
      path: path.resolve(__dirname, 'dist'),
      publicPath: '/',
    },
    module: {
      rules: [
        {
          test: /\.(ts|tsx)$/i,
          loader: 'ts-loader',
          exclude: /node_modules/,
        },
        {
          test: /\.(eot|svg|ttf|woff|woff2|png|jpg|gif)$/i,
          type: 'asset',
        },
        {
          test: /\.html$/i,
          exclude: /index\.html$/,
          use: ['html-loader'],
        },
        // CSS는 환경별로 분리 (dev: style-loader, prod: MiniCssExtractPlugin)
      ],
    },
    resolve: {
      extensions: ['.tsx', '.ts', '.jsx', '.js', '...'],
      alias: {
        '@app': path.resolve(__dirname, 'src/app'),
        '@pages': path.resolve(__dirname, 'src/pages'),
        '@widgets': path.resolve(__dirname, 'src/widgets'),
        '@features': path.resolve(__dirname, 'src/features'),
        '@entities': path.resolve(__dirname, 'src/entities'),
        '@shared': path.resolve(__dirname, 'src/shared'),
        '@shared/components': path.resolve(__dirname, 'src/shared/components'),
        '@shared/styles': path.resolve(__dirname, 'src/shared/styles'),
        '@shared/types': path.resolve(__dirname, 'src/shared/types'),
        '@icons': path.resolve(__dirname, 'assets/icon'),
        '@mocks': path.resolve(__dirname, 'src/mocks'),
        '@config': path.resolve(__dirname, 'src/config'),
        '@sb': path.resolve(__dirname, '.storybook'),
      },
    },
    plugins: [
      new HtmlWebpackPlugin({
        template: 'index.html',
        templateParameters: envVars,
        favicon: './assets/icon/logo-icon.svg',
      }),
    ],
  };
}
