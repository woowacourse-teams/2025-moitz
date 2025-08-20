/* eslint-disable no-undef */
import React from 'react';
import { createRoot, type Root } from 'react-dom/client';

/* ===========================================
 * CustomOverlay
 *   - React 컴포넌트를 네이버 지도 위에 표시하는 오버레이
 * =========================================== */
type CustomOverlayProps = {
  position: naver.maps.LatLng;
  naverMap: naver.maps.Map;
  content: React.ReactNode;
  zIndex?: number;
};

class CustomOverlay extends window.naver.maps.OverlayView {
  private position: naver.maps.LatLng;
  private container: HTMLDivElement;
  private reactRoot: Root;

  constructor({
    position,
    naverMap,
    content,
    zIndex = 100,
  }: CustomOverlayProps) {
    super();

    this.position = position;

    this.container = document.createElement('div');
    this.container.style.position = 'absolute';
    this.container.style.zIndex = String(zIndex);

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
}

export default CustomOverlay;
