import { withLayout } from '../../../../../.storybook/decorators/withLayout'; // TODO: 추후 경로 별칭으로 수정

import BaseLoading from './BaseLoading';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/loading/BaseLoading',
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
