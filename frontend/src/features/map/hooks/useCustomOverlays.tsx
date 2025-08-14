/* eslint-disable no-undef */
import React, { useEffect, useRef } from 'react';

import type {
  RecommendedLocation,
  StartingPlace,
} from '@entities/types/Location';

import MarkerIndex from '@shared/components/markerIndex/MarkerIndex';
import { colorToken } from '@shared/styles/tokens';

import { CustomOverlay } from '../lib/CustomOverlay';

/** 인덱스를 A, B, C...로 변환 (출발지 마커 라벨용) */
const stringToCharCode = (n: number) => String.fromCharCode(n + 64);

/** ----------------------------------------------------------------
 *  LatLng 타입 편차 흡수 헬퍼
 *  - 일부 선언(env)에선 lat()/lng()가 없고 x/y만 있는 경우가 있어
 *    안전하게 값을 읽도록 헬퍼로 통일
 * ---------------------------------------------------------------- */
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

/** 훅 외부에서 주입받는 프로퍼티 타입 */
interface UseCustomOverlaysProps {
  startingLocations: StartingPlace[]; // 출발지 목록
  recommendedLocations: RecommendedLocation[]; // 추천지 목록
  selectedLocation: RecommendedLocation | null; // 선택된 추천지 (없으면 null)
  changeSelectedLocation: (loc: RecommendedLocation) => void; // 추천지 선택 변경
}

/** ======================================================================================
 *  useCustomOverlays
 *  - 지도는 최초 1회 생성
 *  - 마커 렌더링 정책
 *      · 선택 전(null): 시작점 + 모든 추천지
 *      · 선택 후      : 시작점 + 선택된 추천지 1개만
 *  - 라인 렌더링 정책
 *      · 선택된 추천지의 routes 내 paths를 폴리라인으로 그린다
 *  - 변경 시 기존 오버레이/폴리라인은 모두 정리 (메모리/중복 방지)
 * ====================================================================================== */
export const useCustomOverlays = ({
  startingLocations,
  recommendedLocations,
  selectedLocation,
  changeSelectedLocation,
}: UseCustomOverlaysProps) => {
  /** 지도 컨테이너 & 지도 인스턴스 참조 */
  const mapRef = useRef<HTMLDivElement | null>(null);
  const naverMapRef = useRef<naver.maps.Map | null>(null);

  /** 생성된 오버레이/폴리라인을 추적해서 의존성 변경 시 정리 */
  const overlayInstancesRef = useRef<naver.maps.OverlayView[]>([]);
  const polylineInstancesRef = useRef<naver.maps.Polyline[]>([]);

  /** ----------------------------------------------
   *  지도 & 마커 렌더링
   *  - 선택 상태에 따라 추천지 마커를 필터링
   * ---------------------------------------------- */
  useEffect(() => {
    const { naver } = window;
    if (!naver || !mapRef.current) return;

    const all = [...startingLocations, ...recommendedLocations];
    if (all.length === 0) return;

    // 지도 최초 1회 생성 (재사용)
    if (!naverMapRef.current) {
      const center = new naver.maps.LatLng(all[0].y, all[0].x);
      naverMapRef.current = new naver.maps.Map(mapRef.current, {
        center,
        zoom: 11,
      });
    }
    const map = naverMapRef.current!;

    // 이전 오버레이 정리
    overlayInstancesRef.current.forEach((ov) => ov.setMap(null));
    overlayInstancesRef.current = [];

    // 1) 출발지 마커
    startingLocations.forEach((loc, i) => {
      const overlay = new CustomOverlay({
        naverMap: map,
        position: new naver.maps.LatLng(loc.y, loc.x),
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
      // CustomOverlay가 OverlayView와 호환된다고 가정
      overlayInstancesRef.current.push(
        overlay as unknown as naver.maps.OverlayView,
      );
    });

    // 2) 추천지 마커
    //    선택 전: 전체 표시 / 선택 후: 선택된 추천지만 표시
    recommendedLocations.forEach((loc, i) => {
      if (selectedLocation && loc.id !== selectedLocation.id) return;

      const overlay = new CustomOverlay({
        naverMap: map,
        position: new naver.maps.LatLng(loc.y, loc.x),
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

    // 클린업: 의존성 변경/언마운트 시 오버레이 제거
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

  /** ----------------------------------------------
   *  선택된 추천지의 경로(폴리라인) 렌더링
   *  - selectedLocation 변경에만 반응
   * ---------------------------------------------- */
  useEffect(() => {
    const { naver } = window;
    const map = naverMapRef.current;
    if (!naver || !map) return;

    // 이전 라인 정리
    polylineInstancesRef.current.forEach((pl) => pl.setMap(null));
    polylineInstancesRef.current = [];

    if (!selectedLocation) return;

    // 선택된 추천지의 모든 route → path 구간들을 좌표 배열로 변환
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
          strokeColor: colorToken.orange[1], // 선택 라인 강조 색
          strokeOpacity: 0.95,
        });
        polylineInstancesRef.current.push(polyline);
      }
    });

    // 클린업: selectedLocation 변경/언마운트 시 라인 제거
    return () => {
      polylineInstancesRef.current.forEach((pl) => pl.setMap(null));
      polylineInstancesRef.current = [];
    };
  }, [selectedLocation]);

  /** 이 훅이 반환하는 것은 "지도 컨테이너 ref" 하나 */
  return mapRef;
};

/** 버튼 클릭 영역을 명시적으로 분리 (키보드 포커스/클릭 가능) */
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
