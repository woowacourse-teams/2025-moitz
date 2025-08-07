/* eslint-disable no-undef */
export {};

declare global {
  interface Window {
    naver: typeof naver;
  }

  namespace naver.maps {
    class LatLng {
      constructor(lat: number, lng: number);
    }

    class Map {
      constructor(
        element: HTMLElement | string,
        options?: {
          center: LatLng;
          zoom: number;
        },
      );
    }

    class Marker {
      constructor(options: { map: Map; position: LatLng });
    }

    class OverlayView {
      constructor();
      setMap(map: Map | null): void;
      getMap(): Map | null;
      getPanes(): { overlayLayer: HTMLElement };
      getProjection(): {
        fromCoordToOffset(coord: Coord): { x: number; y: number };
      };
    }

    class Coord {}
  }
}
