import recommendedSpotItemListMock from '../../mocks/recommendedSpotItemListMock';

import SpotItemList from './SpotItemList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/SpotItemList',
  component: SpotItemList,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {},
} satisfies Meta<typeof SpotItemList>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { recommendedSpotItemList: recommendedSpotItemListMock },
};
