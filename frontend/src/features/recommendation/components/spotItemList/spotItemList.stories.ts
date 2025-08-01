import { withLayout } from '../../../../../.storybook/decorators/withLayout';
import recommendedLocationMock from '../../../../mocks/recommendedLocationsMock';

import SpotItemList from './SpotItemList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/SpotItemList',
  component: SpotItemList,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {},
} satisfies Meta<typeof SpotItemList>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { recommendedLocations: recommendedLocationMock },
};
