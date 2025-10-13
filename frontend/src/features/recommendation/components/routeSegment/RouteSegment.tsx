import Dot from '@shared/components/dot/Dot';
import { getLineInfo } from '@shared/lib/getLineInfo';
import { flex, typography } from '@shared/styles/default.styled';
import { colorToken } from '@shared/styles/tokens';

import * as segment from './routeSegment.styled';

interface RouteSegmentProps {
  lineCode: string;
  startStation: string;
  endStation: string;
}

function RouteSegment({
  lineCode,
  startStation,
  endStation,
}: RouteSegmentProps) {
  const lineInfo = getLineInfo(lineCode);
  const subwayColor = lineInfo
    ? colorToken.subway[lineInfo.code]
    : colorToken.gray[6];

  return (
    <div
      css={[flex({ justify: 'flex-start', align: 'center' }), segment.base()]}
    >
      <div
        css={[
          flex({ justify: 'flex-start', align: 'center', gap: 5 }),
          segment.station_name(),
        ]}
      >
        <Dot size={10} colorType="subway" colorTokenIndex={lineInfo.code} />
        <div css={[typography.sh2, segment.title(subwayColor)]}>{lineCode}</div>
      </div>
      <div css={typography.c1}>
        {startStation} - {endStation}
      </div>
    </div>
  );
}

export default RouteSegment;
