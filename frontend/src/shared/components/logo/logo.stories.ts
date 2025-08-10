import Logo from './Logo';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: Logo,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
} satisfies Meta<typeof Logo>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};
