import recommendedSpotItemListMock from '../../../../mocks/recommendedSpotItemListMock';
import startingSpotNameListMock from '../../../../mocks/startingSpotNameListMock';

import BottomSheet from './BottomSheet';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/BottomSheet',
  component: BottomSheet,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    startingSpotNameList: {
      control: { type: 'object' },
      description: '출발지 장소 리스트',
    },
    recommendedSpotItemList: {
      control: { type: 'object' },
      description: '추천 장소 리스트',
    },
  },
} satisfies Meta<typeof BottomSheet>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    startingSpotNameList: startingSpotNameListMock,
    recommendedSpotItemList: recommendedSpotItemListMock,
  },
};

export const Short: Story = {
  args: {
    startingSpotNameList: startingSpotNameListMock,
    recommendedSpotItemList: recommendedSpotItemListMock.slice(0, 2),
  },
};
