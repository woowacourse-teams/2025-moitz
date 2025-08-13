import { RecommendedLocationsMock } from '@mocks/LocationsMock';

import { withContainer } from '@sb/decorators/withContainer';

import SpotItemList from './SpotItemList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
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
    recommendedLocations: RecommendedLocationsMock,
    onSpotClick: () => {},
  },
};
