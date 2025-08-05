import { withContainer } from '../../../../.storybook/decorators/withContainer';

import SpotItem from './SpotItem';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/SpotItem',
  component: SpotItem,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    index: {
      control: { type: 'number', min: 1, max: 5 },
      description: '마커에 표시될 인덱스 번호',
    },
    name: {
      control: { type: 'text' },
      description: '추천 장소 이름',
    },
    description: {
      control: { type: 'text' },
      description: '추천 장소 설명',
    },
    avgMinutes: {
      control: { type: 'number' },
      description: '추천 장소까지 평균 소요 시간(분)',
    },
    isBest: {
      control: { type: 'boolean' },
      description: '추천 장소의 best 여부',
    },
  },
} satisfies Meta<typeof SpotItem>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    index: 1,
    name: '역이름',
    description: '설명입니다.',
    avgMinutes: 10,
    isBest: false,
  },
};

export const Best: Story = {
  args: {
    index: 1,
    name: '역이름',
    description: '설명입니다.',
    avgMinutes: 10,
    isBest: true,
  },
};
