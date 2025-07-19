import Badge from './Badge';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/Badge',
  component: Badge,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      control: { type: 'text' },
      description: '뱃지에 표시될 텍스트',
    },
  },
} satisfies Meta<typeof Badge>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { text: 'best' },
};
