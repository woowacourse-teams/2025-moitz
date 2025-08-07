import recommendedLocationMock from '@mocks/recommendedLocationsMock';

import { withContainer } from '../../../../../.storybook/decorators/withContainer';

import SpotItemList from './SpotItemList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/SpotItemList',
  component: SpotItemList,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    onSpotClick: {
      action: 'onSpotClick',
    },
  },
} satisfies Meta<typeof SpotItemList>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    recommendedLocations: recommendedLocationMock,
    onSpotClick: () => {},
  },
};
