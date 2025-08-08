import { useEffect, useRef } from 'react';
import { Link } from 'react-router';

import { View } from '@features/recommendation/types/bottomSheetView';

import { StartingPlace } from '@entities/types/Location';

import MapButton from '@shared/components/mapButton/MapButton';
import MapPoint from '@shared/components/mapPoint/MapPoint';
import { flex } from '@shared/styles/default.styled';

import IconBack from '@icons/icon-back.svg';

import * as map from './map.styled';

interface MapProps {
  startingLocations: StartingPlace[];
  currentView: View;
  handleBackButtonClick: () => void;
}

function Map({
  startingLocations,
  currentView,
  handleBackButtonClick,
}: MapProps) {
  const mapRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const { naver } = window;

    if (!naver || !mapRef.current) return;

    const locations = startingLocations.map(
      (location) => new naver.maps.LatLng(location.y, location.x),
    );

    const map = new naver.maps.Map(mapRef.current, {
      center: locations[0],
      zoom: 11,
    });

    locations.forEach((location) => {
      new naver.maps.Marker({
        map,
        position: location,
      });
    });
  }, []);

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
