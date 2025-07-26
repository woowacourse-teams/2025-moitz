import spotItemListMock from '../../mocks/spotItemListMock';

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
    itemList: {
      control: { type: 'object' },
      description: '추천 장소 리스트',
    },
  },
} satisfies Meta<typeof BottomSheet>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    itemList: spotItemListMock,
  },
};

export const Short: Story = {
  args: {
    itemList: spotItemListMock.slice(0, 2),
  },
};
