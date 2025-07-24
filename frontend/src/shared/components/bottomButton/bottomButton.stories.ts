import BottomButton from './BottomButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/BottomButton',
  component: BottomButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: { type: 'select' },
      options: ['button', 'submit', 'reset'],
      description: '버튼 타입',
    },
    text: {
      control: { type: 'text' },
      description: '뱃지에 표시될 텍스트',
    },
  },
} satisfies Meta<typeof BottomButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { type: 'button', text: '버튼 텍스트입니다', active: false },
};

export const Active: Story = {
  args: { type: 'button', text: '버튼 텍스트입니다', active: true },
};
