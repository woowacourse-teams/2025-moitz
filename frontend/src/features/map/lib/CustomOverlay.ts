import ReactDOM from 'react-dom/client';

declare global {
  interface Window {
    naver: typeof naver;
  }
}
export class CustomOverlay extends window.naver.maps.OverlayView {
  private position: naver.maps.LatLng;
  private container: HTMLDivElement;
  private reactRoot: ReactDOM.Root;

  constructor({
    position,
    naverMap,
    content,
  }: {
    position: naver.maps.LatLng;
    naverMap: naver.maps.Map;
    content: React.ReactElement;
  }) {
    super();

    this.position = position;
    this.container = document.createElement('div');
    this.container.style.position = 'absolute';

    this.reactRoot = ReactDOM.createRoot(this.container);
    this.reactRoot.render(content);

    this.setMap(naverMap);
  }

  onAdd() {
    const overlayLayer = this.getPanes().overlayLayer;
    overlayLayer.appendChild(this.container);
  }

  draw() {
    const projection = this.getProjection();
    const pixelPosition = projection.fromCoordToOffset(this.position);

    this.container.style.left = `${pixelPosition.x}px`;
    this.container.style.top = `${pixelPosition.y}px`;
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

  setMap(map: naver.maps.Map | null) {
    super.setMap(map);
  }
}
