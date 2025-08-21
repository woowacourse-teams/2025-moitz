import { Link } from 'react-router';

import { useCustomOverlays } from '@features/map/hooks/useCustomOverlays';
import { SelectedLocation } from '@features/recommendation/types/SelectedLocation';
import Toast from '@features/toast/components/Toast';
import { useToast } from '@features/toast/hooks/useToast';

import {
  RecommendedLocation,
  StartingPlace,
} from '@entities/location/types/Location';

import MapButton from '@shared/components/mapButton/MapButton';
import MapPoint from '@shared/components/mapPoint/MapPoint';
import { flex } from '@shared/styles/default.styled';

import IconBack from '@icons/icon-back.svg';
import IconShare from '@icons/icon-share.svg';

import * as map from './map.styled';

const DEFAULT_CURRENT_RECOMMEND_LOCATION = '전체 추천 지점';

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
  const { isVisible, message, showToast } = useToast();

  const mapRef = useCustomOverlays({
    startingLocations,
    recommendedLocations,
    changeSelectedLocation,
  });

  const handleBackButtonClick = () => {
    changeSelectedLocation(null);
  };

  const handleShareButtonClick = () => {
    navigator.clipboard.writeText(window.location.href);
    showToast('링크가 복사되었습니다.');
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
        <MapPoint
          text={
            selectedLocation
              ? selectedLocation.name
              : DEFAULT_CURRENT_RECOMMEND_LOCATION
          }
        />
        <MapButton
          src={IconShare}
          alt="share"
          onClick={handleShareButtonClick}
        />
      </div>
      <Toast message={message} isVisible={isVisible} />
    </div>
  );
}

export default Map;
