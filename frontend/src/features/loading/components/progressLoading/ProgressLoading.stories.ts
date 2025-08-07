import { withLayout } from '../../../../../.storybook/decorators/withLayout'; // TODO: 추후 경로 별칭으로 수정

import ProgressLoading from './ProgressLoading';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/loading/ProgressLoading',
  component: ProgressLoading,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
} satisfies Meta<typeof ProgressLoading>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    isLoading: true,
  },
};
