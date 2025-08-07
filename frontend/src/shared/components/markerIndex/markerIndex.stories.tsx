import MarkerIndex from './MarkerIndex';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/MarkerIndex',
  component: MarkerIndex,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    index: {
      description: '마커에 표시될 인덱스 번호',
    },
    type: {
      control: { type: 'radio' },
      options: ['recommended', 'starting'],
      description: "'recommended' 또는 'starting' 타입",
    },
    label: {
      control: 'text',
      description: '하단에 표시할 라벨 텍스트',
    },
    hasStroke: {
      control: 'boolean',
      description: '마커에 테두리 추가 여부',
    },
    hasShadow: {
      control: 'boolean',
      description: '마커에 그림자 추가 여부',
    },
  },
} satisfies Meta<typeof MarkerIndex>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Case1_Recommended_All: Story = {
  name: 'Case1 - 추천 장소, 라벨 + 스트로크 + 그림자',
  args: {
    index: 1,
    type: 'recommended',
    label: '추천 장소',
    hasStroke: true,
    hasShadow: true,
  },
};

export const Case2_Recommended_None: Story = {
  name: 'Case2 - 추천 장소, 아무것도 없음',
  args: {
    index: 2,
    type: 'recommended',
    label: '',
    hasStroke: false,
    hasShadow: false,
  },
};

export const Case3_Starting_All: Story = {
  name: 'Case3 - 출발지, 라벨 + 스트로크 + 그림자',
  args: {
    index: 'A',
    type: 'starting',
    label: '출발역',
    hasStroke: true,
    hasShadow: true,
  },
};

export const Case4_Starting_StrokeShadow: Story = {
  name: 'Case4 - 출발지, 스트로크 + 그림자',
  args: {
    index: 'B',
    type: 'starting',
    label: '',
    hasStroke: true,
    hasShadow: true,
  },
};
