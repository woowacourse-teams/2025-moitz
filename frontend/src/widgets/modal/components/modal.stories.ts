import { withLayout } from '@sb/decorators/withLayout';

import Modal from './Modal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: Modal,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    onClose: {
      description: '모달 닫기 함수',
    },
    children: {
      description: '모달 내부 컨텐츠 내용',
    },
  },
} satisfies Meta<typeof Modal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    onClose: () => {},
    children: '모달 내용이 들어갑니다',
  },
};
