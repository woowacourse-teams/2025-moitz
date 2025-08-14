/* eslint-disable no-undef */
import React, { useEffect, useRef } from 'react';
import { createRoot, type Root } from 'react-dom/client';

import type {
  RecommendedLocation,
  StartingPlace,
} from '@entities/types/Location';

import MarkerIndex from '@shared/components/markerIndex/MarkerIndex';
import { colorToken } from '@shared/styles/tokens';

/** 인덱스를 A, B, C...로 변환 (출발지 마커 라벨용) */
const stringToCharCode = (n: number) => String.fromCharCode(n + 64);

/* -----------------------------
 * LatLng 타입 편차 흡수 헬퍼
 * - lat()/lng() 또는 x/y 모두 대응
 * ----------------------------- */
type LatLngLike =
  | naver.maps.LatLng
  | { lat: () => number; lng: () => number }
  | { x: number; y: number };

function getLat(pt: LatLngLike): number {
  return typeof (pt as any).lat === 'function'
    ? (pt as { lat: () => number }).lat()
    : (pt as { y: number }).y;
}
function getLng(pt: LatLngLike): number {
  return typeof (pt as any).lng === 'function'
    ? (pt as { lng: () => number }).lng()
    : (pt as { x: number }).x;
}
function isSameLatLng(a: LatLngLike, b: LatLngLike) {
  return getLat(a) === getLat(b) && getLng(a) === getLng(b);
}

/* ===========================================
 * React용 CustomOverlay (zIndex 지원)
 *  - content: React 노드를 그대로 붙여 렌더
 *  - zIndex: 옵션, 기본 100
 * =========================================== */

type CustomOverlayProps = {
  position: naver.maps.LatLng;
  naverMap: naver.maps.Map;
  content: React.ReactNode;
  zIndex?: number;
  className?: string;
  pointerEvents?: 'auto' | 'none';
};

class CustomOverlay extends window.naver.maps.OverlayView {
  private position: naver.maps.LatLng;
  private container: HTMLDivElement;
  private reactRoot: Root;
  private zIndex: number;

  constructor({
    position,
    naverMap,
    content,
    zIndex = 100,
    className,
    pointerEvents = 'auto',
  }: CustomOverlayProps) {
    super();

    this.position = position;
    this.zIndex = zIndex;

    this.container = document.createElement('div');
    this.container.style.position = 'absolute';
    this.container.style.pointerEvents = pointerEvents;
    this.container.style.zIndex = String(zIndex);
    if (className) this.container.className = className;

    this.reactRoot = createRoot(this.container);
    this.reactRoot.render(content);

    this.setMap(naverMap);
  }

  onAdd() {
    const { overlayLayer } = this.getPanes();
    overlayLayer.appendChild(this.container);
  }

  draw() {
    const projection = this.getProjection();
    const pixel = projection.fromCoordToOffset(
      this.position as unknown as naver.maps.Coord,
    );
    this.container.style.left = `${pixel.x}px`;
    this.container.style.top = `${pixel.y}px`;
  }

  onRemove() {
    this.reactRoot.unmount();
    this.container.remove();
  }

  setPosition(position: naver.maps.LatLng) {
    this.position = position;
    this.draw();
  }

  getPosition() {
    return this.position;
  }

  setZIndex(z: number) {
    this.zIndex = z;
    this.container.style.zIndex = String(z);
  }

  setMap(map: naver.maps.Map | null) {
    super.setMap(map);
  }
}

/* ===========================================
 * useCustomOverlays
 *  - 선택 전: 시작점 + 모든 추천지
 *  - 선택 후: 시작점 + 선택된 추천지 1개만
 *  - 선택된 추천지 경로만 폴리라인으로 렌더
 *  - 마커(zIndex=300/200) > 라인(zIndex=1)
 * =========================================== */
interface UseCustomOverlaysProps {
  startingLocations: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  selectedLocation: RecommendedLocation | null;
  changeSelectedLocation: (loc: RecommendedLocation) => void;
}

export const useCustomOverlays = ({
  startingLocations,
  recommendedLocations,
  selectedLocation,
  changeSelectedLocation,
}: UseCustomOverlaysProps) => {
  const mapRef = useRef<HTMLDivElement | null>(null);
  const naverMapRef = useRef<naver.maps.Map | null>(null);

  const overlayInstancesRef = useRef<naver.maps.OverlayView[]>([]);
  const polylineInstancesRef = useRef<naver.maps.Polyline[]>([]);

  // 지도 & 마커 렌더링
  useEffect(() => {
    const { naver } = window;
    if (!naver || !mapRef.current) return;

    const all = [...startingLocations, ...recommendedLocations];
    if (all.length === 0) return;

    // 지도 최초 1회 생성
    if (!naverMapRef.current) {
      const center = new naver.maps.LatLng(all[0].y, all[0].x);
      naverMapRef.current = new naver.maps.Map(mapRef.current, {
        center,
        zoom: 11,
      });
    }
    const map = naverMapRef.current!;

    // 기존 오버레이 제거
    overlayInstancesRef.current.forEach((ov) => ov.setMap(null));
    overlayInstancesRef.current = [];

    // 1) 출발지 마커 (항상 표시) — 가장 위로
    startingLocations.forEach((loc, i) => {
      const overlay = new CustomOverlay({
        naverMap: map,
        position: new naver.maps.LatLng(loc.y, loc.x),
        zIndex: 300,
        content: (
          <MarkerIndex
            index={stringToCharCode(i + 1)}
            type="starting"
            label={loc.name}
            hasStroke
            hasShadow
          />
        ),
      });
      overlayInstancesRef.current.push(
        overlay as unknown as naver.maps.OverlayView,
      );
    });

    // 2) 추천지 마커
    //    - 선택 전(null): 모두 표시
    //    - 선택 후: 선택된 추천지만 표시 (라인 위로 오도록 zIndex 200)
    recommendedLocations.forEach((loc, i) => {
      if (selectedLocation && loc.id !== selectedLocation.id) return;

      const overlay = new CustomOverlay({
        naverMap: map,
        position: new naver.maps.LatLng(loc.y, loc.x),
        zIndex: 200,
        content: (
          <MarkerButton onClick={() => changeSelectedLocation(loc)}>
            <MarkerIndex
              index={i + 1}
              type="recommended"
              label={loc.name}
              hasStroke
              hasShadow
            />
          </MarkerButton>
        ),
      });
      overlayInstancesRef.current.push(
        overlay as unknown as naver.maps.OverlayView,
      );
    });

    return () => {
      overlayInstancesRef.current.forEach((ov) => ov.setMap(null));
      overlayInstancesRef.current = [];
    };
  }, [
    startingLocations,
    recommendedLocations,
    selectedLocation,
    changeSelectedLocation,
  ]);

  // 선택된 추천지의 경로(폴리라인) 렌더링
  useEffect(() => {
    const { naver } = window;
    const map = naverMapRef.current;
    if (!naver || !map) return;

    // 기존 라인 제거
    polylineInstancesRef.current.forEach((pl) => pl.setMap(null));
    polylineInstancesRef.current = [];

    if (!selectedLocation) return;

    (selectedLocation.routes ?? []).forEach((route) => {
      const raw: naver.maps.LatLng[] = (route.paths ?? []).flatMap((seg) => [
        new naver.maps.LatLng(seg.startingY, seg.startingX),
        new naver.maps.LatLng(seg.endingY, seg.endingX),
      ]);

      // 연속 중복 좌표 제거
      const path: naver.maps.LatLng[] = [];
      for (let i = 0; i < raw.length; i++) {
        if (i === 0 || !isSameLatLng(raw[i], raw[i - 1])) path.push(raw[i]);
      }

      if (path.length >= 2) {
        const polyline = new naver.maps.Polyline({
          map,
          path,
          strokeWeight: 5,
          strokeColor: colorToken.main[1],
          strokeOpacity: 0.95,
          zIndex: 1,
        });
        polylineInstancesRef.current.push(polyline);
      }
    });

    return () => {
      polylineInstancesRef.current.forEach((pl) => pl.setMap(null));
      polylineInstancesRef.current = [];
    };
  }, [selectedLocation]);

  // 지도 컨테이너 ref만 반환
  return mapRef;
};

/** 버튼 클릭 영역 (접근성 고려해 명시적 버튼 사용) */
interface MarkerButtonProps {
  children: React.ReactNode;
  onClick: () => void;
}
function MarkerButton({ children, onClick }: MarkerButtonProps) {
  return (
    <button type="button" onClick={onClick}>
      {children}
    </button>
  );
}
