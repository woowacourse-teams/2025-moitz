const path = require('path');

const dotenv = require('dotenv');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');


// .env 파일 읽기
const envVars = dotenv.config().parsed || {};

console.log(envVars.NAVER_MAP_API_KEY);

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
  },
  devServer: {
    open: true,
    host: 'localhost',
    historyApiFallback: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: 'index.html',
      templateParameters: envVars, // HTML에서 <%= BASE_URL %> 사용 가능
    }),
    new webpack.DefinePlugin(defineEnv), // JS에서 process.env.BASE_URL 사용 가능
  ],
  module: {
    rules: [
      {
        test: /\.(ts|tsx)$/i,
        loader: 'ts-loader',
        exclude: ['/node_modules/'],
      },
      {
        test: /\.(eot|svg|ttf|woff|woff2|png|jpg|gif)$/i,
        type: 'asset',
      },
      {
        test: /\.html$/i,
        exclude: /index\.html$/, // HtmlWebpackPlugin이 처리하도록 제외
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
      '@shared': path.resolve(__dirname, 'src/shared'),
      '@shared/components': path.resolve(__dirname, 'src/shared/components'),
      '@shared/styles': path.resolve(__dirname, 'src/shared/styles'),
      '@shared/types': path.resolve(__dirname, 'src/shared/types'),
      '@icons': path.resolve(__dirname, 'assets/icon'),
    },
  },
};

config.mode = isProduction ? 'production' : 'development';

module.exports = config;
