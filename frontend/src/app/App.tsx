import { Route, Routes } from 'react-router';

import IndexPage from '@pages/indexPage/IndexPage';
import NotFoundPage from '@pages/notFoundPage/NotFoundPage';
import ResultPage from '@pages/resultPage/ResultPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<IndexPage />} />
      <Route path="/result" element={<ResultPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
