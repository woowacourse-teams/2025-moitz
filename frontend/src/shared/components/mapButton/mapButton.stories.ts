import IconBack from '@icons/icon-back.svg';
import IconShare from '@icons/icon-share.svg';

import MapButton from './MapButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: MapButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    src: {
      control: { type: 'text' },
      description: '이미지 경로',
    },
    alt: {
      control: { type: 'text' },
      description: '이미지 설명',
    },
    onClick: {
      action: 'onClick',
    },
  },
} satisfies Meta<typeof MapButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Back: Story = {
  args: { src: IconBack, alt: '뒤로가기', onClick: () => {} },
};

export const Share: Story = {
  args: { src: IconShare, alt: '공유하기', onClick: () => {} },
};
