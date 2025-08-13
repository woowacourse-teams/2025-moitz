import { withContainer } from '@sb/decorators/withContainer';

import ProgressText from './ProgressText';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/loading/ProgressText',
  component: ProgressText,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  args: {
    text: ['테스트1', '테스트2', '테스트3'],
  },
} satisfies Meta<typeof ProgressText>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    text: ['테스트1', '테스트2', '테스트3'],
  },
};
