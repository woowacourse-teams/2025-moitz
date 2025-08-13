import * as Sentry from '@sentry/react';
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router';

import { LocationsProvider } from '@entities/contexts/LocationsProvider';

import App from './src/app/App';
import Layout from './src/shared/components/layout/Layout';
import GlobalStyle from './src/shared/styles/GlobalStyle';

if (process.env.NODE_ENV === 'production') {
  Sentry.init({
    dsn: process.env.SENTRY_DSN,
    // Setting this option to true will send default PII data to Sentry.
    // For example, automatic IP address collection on events
    sendDefaultPii: true,
    integrations: [
      Sentry.feedbackIntegration({
        colorScheme: 'system',
      }),
    ],
  });
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <GlobalStyle />
    <LocationsProvider>
      <BrowserRouter>
        <Layout>
          <App />
        </Layout>
      </BrowserRouter>
    </LocationsProvider>
  </React.StrictMode>,
);
