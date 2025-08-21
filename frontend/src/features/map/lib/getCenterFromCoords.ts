export type Coord = { x: number; y: number };

export function getCenterFromCoords(coords: Coord[]) {
  if (coords.length === 0) {
    throw new Error('coords 배열이 비어 있습니다.');
  }

  let minY = coords[0].y;
  let maxY = coords[0].y;
  let minX = coords[0].x;
  let maxX = coords[0].x;

  for (let i = 1; i < coords.length; i++) {
    const { x, y } = coords[i];
    if (y < minY) minY = y;
    if (y > maxY) maxY = y;
    if (x < minX) minX = x;
    if (x > maxX) maxX = x;
  }

  const centerY = (minY + maxY) / 2;
  const centerX = (minX + maxX) / 2;

  return {
    centerCoord: { x: centerX, y: centerY },
  };
}
