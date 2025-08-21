import { withLayout } from '@sb/decorators/withLayout';

import ConditionSelector from './ConditionSelector';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: ConditionSelector,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    selectedConditionID: {
      control: { type: 'text' },
      description: '조건 ID',
    },
    onSelect: {
      description: '조건 ID 업데이트 시 실행될 함수',
    },
  },
} satisfies Meta<typeof ConditionSelector>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    selectedConditionID: '',
    onSelect: () => {},
  },
};
