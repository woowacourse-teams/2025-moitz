import { Link } from 'react-router';

import { useCustomOverlays } from '@features/map/hooks/useCustomOverlays';
import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import MapButton from '@shared/components/mapButton/MapButton';
import MapPoint from '@shared/components/mapPoint/MapPoint';
import { flex } from '@shared/styles/default.styled';

import IconBack from '@icons/icon-back.svg';

import * as map from './map.styled';

interface MapProps {
  recommendedLocations: RecommendedLocation[];
  startingLocations: StartingPlace[];
  currentView: View;
  handleBackButtonClick: () => void;
}

function Map({
  startingLocations,
  recommendedLocations,
  currentView,
  handleBackButtonClick,
}: MapProps) {
  const mapRef = useCustomOverlays({
    startingLocations,
    recommendedLocations,
  });

  return (
    <div css={map.container()}>
      <div ref={mapRef} css={map.base()} />
      <div css={[flex({ justify: 'space-between' }), map.top_overlay()]}>
        {currentView === 'list' ? (
          <Link to="/">
            <MapButton src={IconBack} alt="back" />
          </Link>
        ) : (
          <MapButton
            src={IconBack}
            alt="back"
            onClick={handleBackButtonClick}
          />
        )}
      </div>
      <div css={[flex({ justify: 'space-between' }), map.bottom_overlay()]}>
        <MapPoint text="전체 추첨 지점" />
      </div>
    </div>
  );
}

export default Map;
