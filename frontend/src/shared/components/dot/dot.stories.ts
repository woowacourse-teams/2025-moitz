import Dot from './Dot';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/Dot',
  component: Dot,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: { type: 'number' },
      description: '점의 크기',
    },
    colorType: {
      control: { type: 'select' },
      options: ['gray', 'main', 'sub', 'orange', 'bg'],
      description: '점의 색상',
    },
    colorTokenIndex: {
      control: { type: 'number' },
      description: '점의 색상 인덱스',
    },
  },
} satisfies Meta<typeof Dot>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    size: 10,
    colorType: 'gray',
    colorTokenIndex: 5,
  },
};

export const Active: Story = {
  args: {
    size: 10,
    colorType: 'main',
    colorTokenIndex: 1,
  },
};
