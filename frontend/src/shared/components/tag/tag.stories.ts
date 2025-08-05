import Tag from './Tag';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/Tag',
  component: Tag,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      description: '태그에 표시될 텍스트',
    },
    onClick: {
      description: '태그 삭제 버튼 클릭 시 실행될 함수',
    },
  },
} satisfies Meta<typeof Tag>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    text: '글자다',
    onClick: () => {},
  },
};
