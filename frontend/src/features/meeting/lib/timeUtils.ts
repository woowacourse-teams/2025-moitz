export type Time12Hour = {
  meridiem: '오전' | '오후';
  hour: number; // 1-12
  minute: number; // 0, 10, 20, 30, 40, 50
};

export type Time24Hour = {
  hour: number; // 0-23
  minute: number; // 0, 10, 20, 30, 40, 50
};

/**
 * 24시간 → 12시간 변환
 */
export function convert24to12(time24: Time24Hour) {
  const { hour, minute } = time24;

  if (hour === 0) {
    return { meridiem: '오전' as const, hour: 12, minute };
  } else if (hour <= 12) {
    return { meridiem: '오전' as const, hour, minute };
  } else {
    return { meridiem: '오후' as const, hour: hour - 12, minute };
  }
}

/**
 * 12시간 → 24시간 변환
 */
export function convert12to24(time12: Time12Hour): Time24Hour {
  const { meridiem, hour, minute } = time12;

  let hour24: number;
  if (meridiem === '오전') {
    hour24 = hour === 12 ? 0 : hour;
  } else {
    hour24 = hour === 12 ? 12 : hour + 12;
  }

  return { hour: hour24, minute };
}

/**
 * 24시간 → "HH:MM" 문자열
 */
export function format24Hour(time24: Time24Hour): string {
  const { hour, minute } = time24;
  return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
}

/**
 * "HH:MM" 문자열 → 24시간
 */
export function parse24Hour(timeString: string): Time24Hour | null {
  const match = timeString.match(/^(\d{1,2}):(\d{2})$/);
  if (!match) return null;

  const hour = parseInt(match[1]);
  const minute = parseInt(match[2]);

  if (hour < 0 || hour > 23 || ![0, 10, 20, 30, 40, 50].includes(minute)) {
    return null;
  }

  return { hour, minute };
}

/**
 * "오전/오후 HH:MM" 문자열 → 24시간
 */
export function parseDisplayTimeTo24(timeString: string): Time24Hour | null {
  const match = timeString.match(/(오전|오후)\s(\d{1,2}):(\d{2})/);
  if (!match) return null;

  const meridiem = match[1] === '오전' ? '오전' : '오후';
  const hour = parseInt(match[2]);
  const minute = parseInt(match[3]);

  if (hour < 1 || hour > 12 || ![0, 10, 20, 30, 40, 50].includes(minute)) {
    return null;
  }

  return convert12to24({ meridiem, hour, minute });
}

/**
 * 24시간 → "오전/오후 HH:MM" 문자열
 */
export function format24ToDisplay(time24: Time24Hour): string {
  const time12 = convert24to12(time24);
  const koreanMeridiem = time12.meridiem === '오전' ? '오전' : '오후';
  return `${koreanMeridiem} ${time12.hour.toString().padStart(2, '0')} : ${time12.minute.toString().padStart(2, '0')}`;
}
