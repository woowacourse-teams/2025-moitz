import startingLocationsMock from '../../../mocks/startingLocationsMock';

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
    location: {
      control: { type: 'object' },
      description: '출발지 이름 목록',
    },
  },
} satisfies Meta<typeof StartingSpotName>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    location: startingLocationsMock[0],
    isLast: false,
  },
};

export const Last: Story = {
  args: {
    location: startingLocationsMock[0],
    isLast: true,
  },
};
