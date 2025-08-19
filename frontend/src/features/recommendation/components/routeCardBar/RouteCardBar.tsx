import { flex, typography } from '@shared/styles/default.styled';

import * as routeCardBar from './routeCardBar.styled';

interface RoutePathBar {
  index: number;
  lineCode: string;
  travelTime: number;
}

interface RouteCardBarProps {
  paths: RoutePathBar[];
}

function RouteCardBar({ paths }: RouteCardBarProps) {
  const totalTravelTime = paths.reduce((acc, path) => acc + path.travelTime, 0);

  return (
    <div css={routeCardBar.container()}>
      <div
        css={[
          flex({ justify: 'space-between', align: 'center' }),
          routeCardBar.base(),
        ]}
      >
        {paths.map((path) => {
          return (
            <div
              key={path.index}
              css={[
                flex({ justify: 'center', align: 'center' }),
                routeCardBar.path(
                  path.lineCode,
                  path.travelTime,
                  totalTravelTime,
                ),
              ]}
            >
              <span
                css={[
                  flex({ justify: 'center', align: 'center' }),
                  typography.c2,
                  routeCardBar.text(path.lineCode),
                ]}
              >
                {path.travelTime}분
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default RouteCardBar;
