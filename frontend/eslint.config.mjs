import js from '@eslint/js';
import tseslint from '@typescript-eslint/eslint-plugin';
import tsParser from '@typescript-eslint/parser';
import pluginImport from 'eslint-plugin-import';
import pluginReact from 'eslint-plugin-react';
import globals from 'globals';

export default [
  {
    files: ['**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
    plugins: {
      js,
      import: pluginImport,
      '@typescript-eslint': tseslint,
      react: pluginReact,
    },
    languageOptions: {
      globals: globals.browser,
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
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
      'import/order': [
        'error',
        {
          groups: [
            'builtin',
            'external',
            'internal',
            'parent',
            'sibling',
            'index',
            'type',
          ],
          pathGroups: [
            // 1. 외부 라이브러리
            // 2. 상위 레이어 → 하위 레이어 순서
            {
              pattern: '{../,./,/}app/**',
              group: 'internal',
              position: 'before',
            },
            {
              pattern: '{../,./,/}pages/**',
              group: 'internal',
              position: 'before',
            },
            {
              pattern: '{../,./,/}widgets/**',
              group: 'internal',
              position: 'before',
            },
            {
              pattern: '{../,./,/}features/**',
              group: 'internal',
              position: 'before',
            },
            // 3. shared 전체 (types 제외)
            {
              pattern: '{../,./,/}shared/!(types)/**',
              group: 'internal',
              position: 'after',
            },
            // 4. shared/types (타입)
            {
              pattern: '{../,./,/}shared/types/**',
              group: 'internal',
              position: 'after',
            },
            // 5. 스타일 파일들
            {
              pattern: '**/*.{css}',
              group: 'sibling',
              position: 'after',
            },
          ],
          'newlines-between': 'always',
          alphabetize: {
            order: 'asc',
            caseInsensitive: true,
          },
        },
      ],
    },
    // 🚨 ignore 설정
    ignores: ['**/node_modules/**', '**/dist/**', '**/build/**'],
  },
];
