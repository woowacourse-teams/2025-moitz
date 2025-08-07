import recommendedLocationsMock from '@mocks/recommendedLocationsMock';

import PlaceCard from './PlaceCard';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/detail/PlaceCard',
  component: PlaceCard,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    place: {
      control: { type: 'object' },
      description: '장소 정보',
    },
  },
} satisfies Meta<typeof PlaceCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    place: recommendedLocationsMock.recommendedLocations[0].places[0],
  },
};
