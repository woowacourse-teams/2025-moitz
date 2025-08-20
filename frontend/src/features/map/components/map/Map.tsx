import { Link } from 'react-router';

import { useCustomOverlays } from '@features/map/hooks/useCustomOverlays';
import { SelectedLocation } from '@features/recommendation/types/SelectedLocation';

import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import MapButton from '@shared/components/mapButton/MapButton';
import MapPoint from '@shared/components/mapPoint/MapPoint';
import { flex } from '@shared/styles/default.styled';

import IconBack from '@icons/icon-back.svg';

import * as map from './map.styled';

const DEFAULT_CURRENT_RECOMMEND_LOCATION = '전체 추 지점';

interface MapProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  selectedLocation: SelectedLocation;
  changeSelectedLocation: (location: SelectedLocation) => void;
}

function Map({
  startingLocations,
  recommendedLocations,
  selectedLocation,
  changeSelectedLocation,
}: MapProps) {
  const mapRef = useCustomOverlays({
    startingLocations,
    recommendedLocations,
    changeSelectedLocation,
  });

  const handleBackButtonClick = () => {
    changeSelectedLocation(null);
  };

  return (
    <div css={map.container()}>
      <div ref={mapRef} css={map.base()} />
      <div css={[flex({ justify: 'space-between' }), map.top_overlay()]}>
        {!selectedLocation && (
          <Link to="/">
            <MapButton src={IconBack} alt="back" />
          </Link>
        )}
        {selectedLocation && (
          <MapButton
            src={IconBack}
            alt="back"
            onClick={handleBackButtonClick}
          />
        )}
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
