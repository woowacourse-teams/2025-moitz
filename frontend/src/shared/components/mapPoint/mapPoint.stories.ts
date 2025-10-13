import { withContainer } from '@sb/decorators/withContainer';

import MapPoint from './MapPoint';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: MapPoint,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      control: { type: 'text' },
      description: 'MapPoint에 표시될 텍스트',
    },
  },
} satisfies Meta<typeof MapPoint>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { text: '전체 추천 지점' },
};

export const Station: Story = {
  args: { text: '수유역' },
};
