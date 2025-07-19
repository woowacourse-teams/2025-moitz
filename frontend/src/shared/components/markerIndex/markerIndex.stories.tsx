import MarkerIndex from './MarkerIndex';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/MarkerIndex',
  component: MarkerIndex,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    index: {
      control: { type: 'number', min: 1, max: 5 },
      description: '마커에 표시될 인덱스 번호',
    },
  },
} satisfies Meta<typeof MarkerIndex>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    index: 1,
  },
};
