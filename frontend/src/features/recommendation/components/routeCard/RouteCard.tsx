import { RecommendedRoute } from '@entities/types/Location';

import Badge from '@shared/components/badge/Badge';
import MarkerIndex from '@shared/components/markerIndex/MarkerIndex';
import { flex, typography } from '@shared/styles/default.styled';

import RouteCardBar from '../routeCardBar/RouteCardBar';
import RouteCardDetail from '../routeCardDetail/RouteCardDetail';

import * as card from './routeCard.styled';

interface RouteCardProps {
  startingPlaceIndex: string;
  startingPlaceName: string;
  route: RecommendedRoute;
}

function RouteCard({
  startingPlaceIndex,
  startingPlaceName,
  route,
}: RouteCardProps) {
  const barPaths = route.paths.map((path) => {
    return {
      index: path.index,
      lineCode: path.lineCode,
      travelTime: path.travelTime,
    };
  });

  return (
    <div css={[flex({ direction: 'column', gap: 10 }), card.container()]}>
      <div css={flex({ justify: 'space-between', align: 'center' })}>
        <div css={flex({ justify: 'flex-start', align: 'center', gap: 6 })}>
          <MarkerIndex
            index={startingPlaceIndex}
            type="starting"
            hasStroke={true}
          />
          <span css={[typography.h3, card.title()]}>{startingPlaceName}</span>
        </div>
        <div css={flex({ justify: 'flex-end', align: 'center', gap: 6 })}>
          <Badge type="transfer" text={`🚊 환승 ${route.transferCount}회`} />
          <Badge type="transfer" text={`🕐 ${route.totalTravelTime}분`} />
        </div>
      </div>
      <RouteCardBar paths={barPaths} />
      <RouteCardDetail paths={route.paths} />
    </div>
  );
}

export default RouteCard;
