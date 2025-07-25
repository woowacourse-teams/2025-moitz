import React from 'react';
import ReactDOM from 'react-dom/client';
import { HashRouter } from 'react-router';

import App from './src/app/App';
import Layout from './src/shared/components/layout/Layout';
import GlobalStyle from './src/shared/styles/GlobalStyle';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <GlobalStyle />
    <HashRouter>
      <Layout>
        <App />
      </Layout>
    </HashRouter>
  </React.StrictMode>,
);
