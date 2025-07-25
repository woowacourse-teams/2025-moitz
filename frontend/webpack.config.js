// Generated using webpack-cli https://github.com/webpack/webpack-cli

const path = require('path');

const HtmlWebpackPlugin = require('html-webpack-plugin');

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
    }),

    // Add your plugins here
    // Learn more about plugins from https://webpack.js.org/configuration/plugins/
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
        use: ['html-loader'],
      },

      // Add your rules for custom modules here
      // Learn more about loaders from https://webpack.js.org/loaders/
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
if (isProduction) {
  config.mode = 'production';
} else {
  config.mode = 'development';
}

// Add CSS loader rule
config.module.rules.push({
  test: /\.css$/i,
  use: ['style-loader', 'css-loader'],
});

module.exports = config;
