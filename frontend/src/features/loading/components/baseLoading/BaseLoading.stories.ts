import { withLayout } from '@sb/decorators/withLayout';

import BaseLoading from './BaseLoading';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: BaseLoading,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {},
} satisfies Meta<typeof BaseLoading>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};
