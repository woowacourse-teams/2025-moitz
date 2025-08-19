import React, { useEffect, useRef } from 'react';

import type {
  RecommendedLocation,
  StartingPlace,
} from '@entities/types/Location';

import MarkerIndex from '@shared/components/markerIndex/MarkerIndex';
import { numberToCharCode } from '@shared/utils/numberToCharCode';

import { CustomOverlay } from '../lib/CustomOverlay';

interface useCustomOverlaysProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  changeSelectedLocation: (location: RecommendedLocation) => void;
}

export const useCustomOverlays = ({
  startingLocations,
  recommendedLocations,
  changeSelectedLocation,
}: useCustomOverlaysProps) => {
  const mapRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const { naver } = window;
    if (!naver || !mapRef.current) return;

    const allLocations = [...startingLocations, ...recommendedLocations];

    if (allLocations.length === 0) return;

    const center = new naver.maps.LatLng(allLocations[0].y, allLocations[0].x);

    const naverMap = new naver.maps.Map(mapRef.current, {
      center,
      zoom: 11,
    });

    startingLocations.forEach((location, index) => {
      const position = new naver.maps.LatLng(location.y, location.x);

      new CustomOverlay({
        naverMap,
        position,
        content: (
          <MarkerIndex
            index={numberToCharCode(index + 1)}
            type="starting"
            label={location.name}
            hasStroke
            hasShadow
          />
        ),
      });
    });

    recommendedLocations.forEach((location, index) => {
      const position = new naver.maps.LatLng(location.y, location.x);

      new CustomOverlay({
        naverMap,
        position,
        content: (
          <button
            onClick={() => {
              changeSelectedLocation(location);
            }}
          >
            <MarkerIndex
              index={index + 1}
              type="recommended"
              label={location.name}
              hasStroke
              hasShadow
            />
          </button>
        ),
      });
    });
  }, [startingLocations, recommendedLocations]);

  return mapRef;
};
