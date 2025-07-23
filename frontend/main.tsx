import React from 'react';
import ReactDOM from 'react-dom/client';

import App from './src/app/App';
import Layout from './src/shared/components/layout/Layout';
import GlobalStyle from './src/shared/styles/GlobalStyle';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <GlobalStyle />
    <Layout>
      <App />
    </Layout>
  </React.StrictMode>,
);
