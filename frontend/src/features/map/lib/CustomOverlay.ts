/* eslint-disable no-undef */
import { createRoot, type Root } from 'react-dom/client';

type CustomOverlayProps = {
  position: naver.maps.LatLng;
  naverMap: naver.maps.Map;
  content: React.ReactNode; // ReactElement에 한정하지 않아도 됨
  zIndex: number; // ✅ 추가: 마커를 위로 올리기 위한 zIndex
};

export class CustomOverlay extends window.naver.maps.OverlayView {
  private position: naver.maps.LatLng;
  private container: HTMLDivElement;
  private reactRoot: Root;
  private zIndex?: number;

  constructor({ position, naverMap, content, zIndex }: CustomOverlayProps) {
    super();

    this.position = position;
    this.zIndex = zIndex;

    // 컨테이너 DOM
    this.container = document.createElement('div');
    this.container.style.position = 'absolute';
    if (typeof zIndex === 'number')
      this.container.style.zIndex = String(zIndex); // ✅ 적용

    // React 렌더
    this.reactRoot = createRoot(this.container);
    this.reactRoot.render(content);

    // 지도에 추가
    this.setMap(naverMap);
  }

  /** OverlayView 생명주기 — 오버레이가 지도에 추가될 때 호출 */
  onAdd() {
    const { overlayLayer } = this.getPanes();
    overlayLayer.appendChild(this.container);
  }

  /** OverlayView 생명주기 — 위치/줌 변경 시 호출 */
  draw() {
    const projection = this.getProjection();
    const pixel = projection.fromCoordToOffset(
      this.position as unknown as naver.maps.Coord,
    );
    this.container.style.left = `${pixel.x}px`;
    this.container.style.top = `${pixel.y}px`;
  }

  /** OverlayView 생명주기 — 지도에서 제거될 때 호출 */
  onRemove() {
    this.reactRoot.unmount();
    this.container.remove();
  }

  /** 위치 갱신 */
  setPosition(position: naver.maps.LatLng) {
    this.position = position;
    this.draw();
  }

  getPosition() {
    return this.position;
  }

  /** zIndex 런타임 갱신 (선택) */
  setZIndex(z: number) {
    this.zIndex = z;
    this.container.style.zIndex = String(z);
  }

  /** setMap은 부모 구현 호출 그대로 유지(명시적으로 남김) */
  setMap(map: naver.maps.Map | null) {
    super.setMap(map);
  }
}
