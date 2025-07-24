/** @jsxImportSource @emotion/react */

import { useEffect, useRef } from 'react';

import * as map from './map.styled';

function Map() {
  const mapRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const { naver } = window;

    if (!naver || !mapRef.current) return;

    const location = new naver.maps.LatLng(37.3595704, 127.105399);

    const map = new naver.maps.Map(mapRef.current, {
      center: location,
      zoom: 17,
    });

    new naver.maps.Marker({
      map,
      position: location,
    });
  }, []);

  return <div ref={mapRef} css={map.base()} />;
}

export default Map;
