import { RecommendedPath } from '@entities/location/types/Location';

import { flex } from '@shared/styles/default.styled';

import RouteIndicator from '../routeIndicator/RouteIndicator';
import RouteSegment from '../routeSegment/RouteSegment';

interface RouteCardDetailProps {
  paths: RecommendedPath[];
}

function RouteCardDetail({ paths }: RouteCardDetailProps) {
  return (
    <div css={flex({ direction: 'column', gap: 2 })}>
      {paths.map((path) =>
        path.startStation !== path.endStation ? (
          <RouteSegment
            key={path.index}
            lineCode={path.lineCode}
            startStation={path.startStation}
            endStation={path.endStation}
          />
        ) : (
          <RouteIndicator key={path.index} />
        ),
      )}
    </div>
  );
}

export default RouteCardDetail;
