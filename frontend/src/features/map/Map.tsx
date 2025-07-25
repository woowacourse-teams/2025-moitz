/** @jsxImportSource @emotion/react */

import { useEffect, useRef } from 'react';
import { Link } from 'react-router';

import MapButton from '@shared/components/mapButton/MapButton';
import MapPoint from '@shared/components/mapPoint/MapPoint';
import { flex } from '@shared/styles/default.styled';

import IconBack from '@icons/icon-back.svg';
import IconShare from '@icons/icon-share.svg';

import * as map from './map.styled';

function Map() {
  const mapRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const { naver } = window;

    if (!naver || !mapRef.current) return;

    const locations = [
      new naver.maps.LatLng(37.554722, 126.970833),
      new naver.maps.LatLng(37.497942, 127.027621),
      new naver.maps.LatLng(37.513342, 127.100095),
      new naver.maps.LatLng(37.556201, 126.923734),
      new naver.maps.LatLng(37.555134, 126.936893),
    ];

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
        <Link to="/">
          <MapButton src={IconBack} alt="back" />
        </Link>
        <Link to="/">
          <MapButton src={IconShare} alt="share" />
        </Link>
      </div>
      <div css={[flex({ justify: 'space-between' }), map.bottom_overlay()]}>
        <MapPoint text="전체 추첨 지점" />
      </div>
    </div>
  );
}

export default Map;
