import { withLayout } from '@sb/decorators/withLayout';

import ProgressLoading from './ProgressLoading';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: ProgressLoading,
  decorators: [withLayout],
  parameters: {
    layout: 'centered'
  },
  tags: ['autodocs']
} satisfies Meta<typeof ProgressLoading>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    isComplete: false
  }
};

export const Complete: Story = {
  args: {
    isComplete: true
  }
};
