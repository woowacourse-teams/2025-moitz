export const numberToCharCode = (num: number) => {
  return String.fromCharCode(64 + num); // 65는 'A'의 ASCII 코드
};
