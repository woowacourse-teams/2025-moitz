import { pageView } from '@config/gtag';
import Clarity from '@microsoft/clarity';
import { useEffect } from 'react';
import { Route, Routes, useLocation } from 'react-router';

import IndexPage from '@pages/components/indexPage/IndexPage';
import NotFoundPage from '@pages/components/notFoundPage/NotFoundPage';
import ResultPage from '@pages/components/resultPage/ResultPage';

export default function App() {
  if (process.env.NODE_ENV === 'production') {
    const location = useLocation();

    useEffect(() => {
      pageView(new URL(window.location.href));
    }, [location]);

    const projectId = process.env.CLARITY_ID;
    Clarity.init(projectId);
  }

  return (
    <Routes>
      <Route path="/" element={<IndexPage />} />
      <Route path="/result/:id" element={<ResultPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
