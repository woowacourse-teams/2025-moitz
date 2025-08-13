import { withContainer } from '@sb/decorators/withContainer';

import RouteSegment from './RouteSegment';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: RouteSegment,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    lineCode: {
      control: { type: 'text' },
      description: '노선 코드',
    },
    startStation: {
      control: { type: 'text' },
      description: '출발 역 이름',
    },
    endStation: {
      control: { type: 'text' },
      description: '도착 역 이름',
    },
  },
} satisfies Meta<typeof RouteSegment>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    lineCode: '1호선',
    startStation: '동대문',
    endStation: '충무로',
  },
};
