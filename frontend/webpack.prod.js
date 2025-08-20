import dotenv from 'dotenv';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';
import TerserPlugin from 'terser-webpack-plugin';
import webpack from 'webpack';
import { merge } from 'webpack-merge';

import common from './webpack.common.js';

const mode = 'production';

const { parsed: envParsed } = dotenv.config({ path: '.env' }) || {};
const envVars = envParsed || {};

// DefinePlugin에 주입할 형태로 변환 + NODE_ENV 보장
const defineEnv = Object.entries(envVars).reduce(
  (acc, [key, value]) => {
    acc[`process.env.${key}`] = JSON.stringify(value);
    return acc;
  },
  { 'process.env.NODE_ENV': JSON.stringify(mode) },
);

export default merge(common(envVars), {
  mode,
  devtool: 'source-map',
  output: {
    filename: '[name].[contenthash].js',
    assetModuleFilename: 'assets/[hash][ext][query]',
    clean: true,
  },
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: [MiniCssExtractPlugin.loader, 'css-loader'],
      },
    ],
  },
  plugins: [
    new webpack.DefinePlugin(defineEnv),
    new MiniCssExtractPlugin({
      filename: '[name].[contenthash].css',
    }),
  ],
  optimization: {
    minimize: true,
    minimizer: [
      new TerserPlugin({
        extractComments: false,
        terserOptions: {
          compress: { passes: 2 },
          mangle: true,
          format: { comments: false },
        },
      }),
    ],
    splitChunks: { chunks: 'all' },
    runtimeChunk: 'single',
  },
});
