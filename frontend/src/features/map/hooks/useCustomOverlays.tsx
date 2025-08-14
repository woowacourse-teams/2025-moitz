import React, { useEffect, useRef } from 'react';

import type {
  RecommendedLocation,
  StartingPlace,
} from '@entities/types/Location';

import MarkerIndex from '@shared/components/markerIndex/MarkerIndex';

import { CustomOverlay } from '../lib/CustomOverlay';

const stringToCharCode = (number: number) => {
  return String.fromCharCode(number + 64);
};

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
  const naverMapRef = useRef<naver.maps.Map | null>(null); // 지도 인스턴스 보관

  useEffect(() => {
    const { naver } = window;
    if (!naver || !mapRef.current) return;

    const allLocations = [...startingLocations, ...recommendedLocations];
    if (allLocations.length === 0) return;

    // 지도 한 번만 생성
    if (!naverMapRef.current) {
      const center = new naver.maps.LatLng(
        allLocations[0].y,
        allLocations[0].x,
      );
      naverMapRef.current = new naver.maps.Map(mapRef.current, {
        center,
        zoom: 11,
      });
    }

    const map = naverMapRef.current!;

    // 시작점
    startingLocations.forEach((location, index) => {
      const position = new naver.maps.LatLng(location.y, location.x);
      new CustomOverlay({
        naverMap: map,
        position,
        content: (
          <MarkerIndex
            index={stringToCharCode(index + 1)}
            type="starting"
            label={location.name}
            hasStroke
            hasShadow
          />
        ),
      });
    });

    // 추천점
    recommendedLocations.forEach((location, index) => {
      const position = new naver.maps.LatLng(location.y, location.x);
      new CustomOverlay({
        naverMap: map,
        position,
        content: (
          <MarkerButton
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
          </MarkerButton>
        ),
      });
    });

    // 폴리라인: 지도 생성 이후, 여기에서 그리기
    const firstLocation = recommendedLocations[0];
    const firstRoute = firstLocation?.routes?.[0];
    if (firstRoute && firstRoute.paths.length > 0) {
      const pat1h = firstRoute.paths.flatMap((seg) => [
        new naver.maps.LatLng(seg.startingY, seg.startingX),
        new naver.maps.LatLng(seg.endingY, seg.endingX),
      ]);

      new naver.maps.Polyline({
        map,
        path,
        strokeWeight: 5,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
      });
    }
  }, [startingLocations, recommendedLocations]);

  return mapRef;
};

interface MarkerButtonProps {
  children: React.ReactNode;
  onClick: () => void;
}

function MarkerButton({ children, onClick }: MarkerButtonProps) {
  return <button onClick={onClick}>{children}</button>;
}
