import { RecommendedRoute } from '@entities/types/Location';

import Badge from '@shared/components/badge/Badge';
import Dot from '@shared/components/dot/Dot';
import { flex, typography } from '@shared/styles/default.styled';

import RouteCardDetail from '../routeCardDetail/RouteCardDetail';

import * as card from './routeCard.styled';

interface RouteCardProps {
  startingPlaceName: string;
  route: RecommendedRoute;
}

function RouteCard({ startingPlaceName, route }: RouteCardProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 10 }), card.container()]}>
      <div css={flex({ justify: 'space-between', align: 'center' })}>
        <div css={flex({ justify: 'flex-start', align: 'center', gap: 6 })}>
          <Dot size={30} colorType="orange" colorTokenIndex={2} />
          <span css={[typography.h3, card.title()]}>{startingPlaceName}</span>
        </div>
        <div css={flex({ justify: 'flex-end', align: 'center', gap: 6 })}>
          <Badge type="transfer" text={`🚊 환승 ${route.transferCount}회`} />
          <Badge type="transfer" text={`🕐 ${route.totalTravelTime}분`} />
        </div>
      </div>
      <div css={card.bar()}></div>
      <RouteCardDetail paths={route.paths} />
    </div>
  );
}

export default RouteCard;
