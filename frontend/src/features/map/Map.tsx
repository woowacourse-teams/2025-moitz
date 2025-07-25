/** @jsxImportSource @emotion/react */

import { useEffect, useRef } from 'react';

import { spotItem } from '@shared/types/spotItem';

import * as map from './map.styled';

function Map({ itemList }: { itemList?: spotItem[] }) {
  const mapRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const { naver } = window;

    if (!naver || !mapRef.current) return;

    const locations =
      itemList?.map((item) => new naver.maps.LatLng(item.y, item.x)) ?? [];
    if (locations.length === 0) return;

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

  return <div ref={mapRef} css={map.base()} />;
}

export default Map;
