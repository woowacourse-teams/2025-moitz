import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import pluginReact from "eslint-plugin-react";
import { defineConfig } from "eslint/config";


// JSX 파일에서 React를 직접 import하지 않아도 되도록 하려면
// 원래는 'plugin:react/jsx-runtime'을 extends에 추가하면 됐음.
// 근데 현재 Flat config에서는 'plugin:' prefix가 지원되지 않기 때문에 사용할 수 없음.
// 대신, 'react/react-in-jsx-scope' 규칙을 'off'로 설정하여 React import를 강제하지 않도록 함.

export default defineConfig([
  { files: ["**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"], plugins: { js }, extends: ["js/recommended"], },
  { files: ["**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"], languageOptions: { globals: globals.browser}, 
  },
  tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  {
    settings: {
      react: {
        version: "detect", // React 버전을 자동으로 감지
      },
    },
  },
  {
    rules: {
      "react/react-in-jsx-scope": "off",
      "react/jsx-uses-react": "off",
      indent: ['error', 2],
    },
    ignores: ['**/node_modules/**', '**/dist/**', '**/build/**'],
  },
]);
