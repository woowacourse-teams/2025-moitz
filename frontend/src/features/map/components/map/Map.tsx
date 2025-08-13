import { Link } from 'react-router';

import { useCustomOverlays } from '@features/map/hooks/useCustomOverlays';
import { View } from '@features/recommendation/types/bottomSheetView';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import MapButton from '@shared/components/mapButton/MapButton';
import MapPoint from '@shared/components/mapPoint/MapPoint';
import { flex } from '@shared/styles/default.styled';

import IconBack from '@icons/icon-back.svg';
import IconShare from '@icons/icon-share.svg';

import * as map from './map.styled';

const DEFAULT_CURRENT_RECOMMEND_LOCATION = '전체 추첨 지점';

interface MapProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  currentView: View;
  selectedLocation: RecommendedLocation;
  changeSelectedLocation: (location: RecommendedLocation) => void;
  handleBackButtonClick: () => void;
}

function Map({
  startingLocations,
  recommendedLocations,
  selectedLocation,
  changeSelectedLocation,
  currentView,
  handleBackButtonClick,
}: MapProps) {
  const mapRef = useCustomOverlays({
    startingLocations,
    recommendedLocations,
    changeSelectedLocation,
  });

  return (
    <div css={map.container()}>
      <div ref={mapRef} css={map.base()} />
      <div css={[flex({ justify: 'space-between' }), map.top_overlay()]}>
        {currentView === 'list' && (
          <Link to="/">
            <MapButton src={IconBack} alt="back" />
          </Link>
        )}
        {currentView === 'detail' && (
          <MapButton
            src={IconBack}
            alt="back"
            onClick={handleBackButtonClick}
          />
        )}
        <Link to="/">
          <MapButton src={IconShare} alt="share" />
        </Link>
      </div>
      <div css={[flex({ justify: 'space-between' }), map.bottom_overlay()]}>
        <MapPoint
          text={
            selectedLocation
              ? selectedLocation.name
              : DEFAULT_CURRENT_RECOMMEND_LOCATION
          }
        />
      </div>
    </div>
  );
}

export default Map;
