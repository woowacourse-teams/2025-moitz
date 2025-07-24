import { useState } from 'react';

export default function useMeetingInfo() {
  const [startingPlaces, setStartingPlaces] = useState<string[]>([]);
  const [meetingTime, setMeetingTime] = useState<string>('');
  const [requirement, setRequirement] = useState<string>('');

  return {
    startingPlaces,
    meetingTime,
    requirement,
    setStartingPlaces,
    setMeetingTime,
    setRequirement,
  };
}
