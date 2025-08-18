const MEETING_DEPARTURE_LIST = 'meeting:departures';
const MEETING_CONDITION_ID = 'meeting:conditionId';

export function setMeetingStorage(params: {
  departureList: string[];
  conditionID: string;
}) {
  localStorage.setItem(
    MEETING_DEPARTURE_LIST,
    JSON.stringify(params.departureList),
  );
  localStorage.setItem(
    MEETING_CONDITION_ID,
    JSON.stringify(params.conditionID),
  );
}

export function getMeetingStorage(): {
  departureList: string[];
  conditionID: string;
  } {
  const departureList = JSON.parse(
    localStorage.getItem(MEETING_DEPARTURE_LIST),
  );
  const conditionID = JSON.parse(localStorage.getItem(MEETING_CONDITION_ID));

  return { departureList, conditionID };
}
