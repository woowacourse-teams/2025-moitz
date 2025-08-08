import { withContainer } from '../../../../../.storybook/decorators/withContainer';

import Progressbar from './Progressbar';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Features/Progressbar/Progressbar',
  component: Progressbar,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
} satisfies Meta<typeof Progressbar>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    progress: 50,
  },
};

export const Start: Story = {
  args: {
    progress: 0,
  },
};

export const Complete: Story = {
  args: {
    progress: 100,
  },
};
