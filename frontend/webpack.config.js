const HtmlWebpackPlugin = require('html-webpack-plugin');

const path = require('path');

module.exports = {
    mode: "development",
    entry: './main.tsx',
    devtool: 'inline-source-map',
    module: {
        rules: [
        {
            test: /\.tsx?$/,
            use: 'ts-loader',
            exclude: /node_modules/,
        },
        ],
    },
    devServer: {
        static: './dist',
    },
    resolve: {
        extensions: ['.tsx', '.ts', '.js'],
    },
    output: {
        filename: '[name].[contenthash].js', // ✅ chunk 이름+해시 포함
        path: path.resolve(__dirname, 'dist'),
        clean: true,                         // dist 폴더 정리
    },
    optimization: {
        runtimeChunk: 'single',
    },
    plugins: [
    new HtmlWebpackPlugin({
      template: './index.html', // ✅ HTML 템플릿 경로
    }),
  ],
};
