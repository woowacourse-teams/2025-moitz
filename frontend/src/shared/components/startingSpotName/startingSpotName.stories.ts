import startingSpotNameListMock from '../../../mocks/startingSpotNameListMock';

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
    nameInfo: {
      control: { type: 'object' },
      description: '출발지 이름 목록',
    },
  },
} satisfies Meta<typeof StartingSpotName>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    nameInfo: startingSpotNameListMock[0],
    isLast: false,
  },
};

export const NotLast: Story = {
  args: {
    nameInfo: startingSpotNameListMock[0],
    isLast: true,
  },
};
