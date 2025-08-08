import {
  StartingPlacesMock,
  RecommendedLocationsMock,
} from '@mocks/LocationsMock';

import { withContainer } from '../../../../../.storybook/decorators/withContainer';

import BottomSheet from './BottomSheet';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/BottomSheet',
  component: BottomSheet,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    startingLocations: {
      control: { type: 'object' },
      description: '출발지 장소 리스트',
    },
    recommendedLocations: {
      control: { type: 'object' },
      description: '추천 장소 리스트',
    },
    currentView: {
      control: { type: 'select' },
      options: ['list', 'detail'],
    },
    selectedLocation: {
      control: { type: 'object' },
      description: '선택된 장소',
    },
    handleSpotClick: {
      action: 'handleSpotClick',
    },
  },
} satisfies Meta<typeof BottomSheet>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    startingLocations: StartingPlacesMock,
    recommendedLocations: RecommendedLocationsMock,
    currentView: 'list',
    selectedLocation: null,
    handleSpotClick: () => {},
  },
};

export const Short: Story = {
  args: {
    startingLocations: StartingPlacesMock,
    recommendedLocations: RecommendedLocationsMock.slice(0, 2),
    currentView: 'list',
    selectedLocation: null,
    handleSpotClick: () => {},
  },
};
