import Textarea from './Textarea';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/Textarea',
  component: Textarea,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    placeholder: {
      control: { type: 'text' },
      description: '인풋 필드에 표시될 텍스트',
    },
    value: {
      control: { type: 'text' },
      description: '인풋 필드에 표시될 텍스트',
    },
    onChange: {
      control: { type: 'text' },
      description: '인풋 필드에 표시될 텍스트',
    },
  },
} satisfies Meta<typeof Textarea>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { placeholder: '아무 값도 없어요', value: '', onChange: () => {} },
};
