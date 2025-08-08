import { StartingPlacesMock } from '@mocks/LocationsMock';

import StartingSpotName from './StartingSpotName';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/StartingSpotName',
  component: StartingSpotName,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    place: {
      control: { type: 'object' },
      description: '출발지 이름 목록',
    },
  },
} satisfies Meta<typeof StartingSpotName>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    place: StartingPlacesMock[0],
    isLast: false,
  },
};

export const Last: Story = {
  args: {
    place: StartingPlacesMock[0],
    isLast: true,
  },
};
