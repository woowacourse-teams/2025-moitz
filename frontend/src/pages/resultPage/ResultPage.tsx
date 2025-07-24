import { useLocation } from 'react-router';

interface StartingPlace {
  index: number;
  name: string;
}

interface LocationState {
  startingPlaces: StartingPlace[];
}

function ResultPage() {
  const location = useLocation();
  const state = location.state as LocationState;

  // 전달받은 데이터 확인
  console.log('전달받은 startingPlaces:', state?.startingPlaces);

  return <div></div>;
}

export default ResultPage;
