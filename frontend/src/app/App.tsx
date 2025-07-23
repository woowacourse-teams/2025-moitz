import { Route, Routes } from 'react-router';

import IndexPage from '../pages/indexPage/IndexPage';
import ResultPage from '../pages/resultPage/ResultPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<IndexPage />} />
      <Route path="/result" element={<ResultPage />} />
    </Routes>
  );
}
