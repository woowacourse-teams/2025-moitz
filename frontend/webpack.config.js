import path from 'node:path';
import { fileURLToPath } from 'node:url';

import dotenv from 'dotenv';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import webpack from 'webpack';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const envVars = dotenv.config().parsed || {};

// DefinePlugin용 환경변수 정제
const defineEnv = Object.entries(envVars).reduce((acc, [key, value]) => {
  acc[`process.env.${key}`] = JSON.stringify(value);
  return acc;
}, {});

const isProduction = process.env.NODE_ENV === 'production';

const config = {
  entry: './main.tsx',
  output: {
    path: path.resolve(__dirname, 'dist'),
    publicPath: '/',
  },
  devServer: {
    open: true,
    host: 'localhost',
    historyApiFallback: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'index.html',
      templateParameters: envVars,
      favicon: './assets/icon/logo-icon.svg',
    }),
    new webpack.DefinePlugin(defineEnv),
  ],
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
      {
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
      },
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
  mode: isProduction ? 'production' : 'development',
};

export default config;
