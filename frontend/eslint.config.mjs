import js from '@eslint/js';
import * as tseslint from '@typescript-eslint/eslint-plugin';
import * as pluginReact from 'eslint-plugin-react';
import globals from 'globals';
import { defineConfig } from 'eslint-define-config';

export default defineConfig([
  {
    files: ['**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
    plugins: { js },
    extends: ['js/recommended'],
  },
  {
    files: ['**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
    languageOptions: { globals: globals.browser },
  },
  tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  {
    // 🚨 react version 명시
    settings: {
      react: {
        version: 'detect',
      },
    },
  },
  {
    // 🚨 'react/react-in-jsx-scope' 규칙을 'off'로 설정
    rules: {
      'react/react-in-jsx-scope': 'off',
      'react/jsx-uses-react': 'off',
      indent: ['error', 2],
    },
    // 🚨 ignore 설정
    ignores: ['**/node_modules/**', '**/dist/**', '**/build/**'],
  },
]);
