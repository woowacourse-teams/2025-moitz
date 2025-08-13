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

    // Polyline
    type StrokeStyle =
      | 'solid'
      | 'dash'
      | 'shortdash'
      | 'longdash'
      | 'dot'
      | 'dashdot';

    interface PolylineOptions {
      map: Map;
      path: LatLng[];
      strokeWeight?: number;
      strokeColor?: string;
      strokeOpacity?: number;
      strokeStyle?: StrokeStyle;
      clickable?: boolean;
      zIndex?: number;
      lineCap?: 'butt' | 'round' | 'square';
    }

    class Polyline {
      constructor(options: PolylineOptions);
      setMap(map: Map | null): void;
      setOptions(options: Partial<PolylineOptions>): void;
      getPath(): LatLng[];
    }
  }
}
