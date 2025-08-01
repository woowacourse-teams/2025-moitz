import Input from './Input';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/Input',
  component: Input,
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
      description: '인풋 필드 값 변경 시 실행될 함수',
    },
    onKeyDown: {
      description: '인풋 필드에 키 입력 시 실행될 함수',
    },
  },
} satisfies Meta<typeof Input>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    placeholder: '아무 값도 없어요',
    value: '',
    onChange: () => {},
    onKeyDown: () => {},
  },
};
