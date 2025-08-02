export {};

declare global {
  interface Window {
    naver: typeof naver;
  }

  namespace naver {
    namespace maps {
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
    }
  }
}
