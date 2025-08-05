import { withContainer } from '../../../../.storybook/decorators/withContainer';
import startSpotNameListMock from '../../../mocks/startingLocationsMock';

import StartingSpotWrapper from './StartingSpotWrapper';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/StartingSpotWrapper',
  component: StartingSpotWrapper,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    startingLocations: {
      control: { type: 'object' },
      description: '출발지 이름 목록',
    },
  },
} satisfies Meta<typeof StartingSpotWrapper>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    startingLocations: startSpotNameListMock,
  },
};

export const Long: Story = {
  args: {
    startingLocations: [
      { index: 0, name: '서울역', id: 0, x: 0, y: 0 },
      { index: 1, name: '강남역', id: 1, x: 1, y: 1 },
      { index: 2, name: '역삼역', id: 2, x: 2, y: 2 },
      { index: 3, name: '잠실역', id: 3, x: 3, y: 3 },
      { index: 4, name: '홍대입구역', id: 4, x: 4, y: 4 },
      { index: 5, name: '신촌역', id: 5, x: 5, y: 5 },
      { index: 6, name: '을지로입구역', id: 6, x: 6, y: 6 },
      { index: 7, name: '종각역', id: 7, x: 7, y: 7 },
      { index: 8, name: '시청역', id: 8, x: 8, y: 8 },
      { index: 9, name: '광화문역', id: 9, x: 9, y: 9 },
      { index: 10, name: '안국역', id: 10, x: 10, y: 10 },
      { index: 11, name: '종로3가역', id: 11, x: 11, y: 11 },
      { index: 12, name: '을지로3가역', id: 12, x: 12, y: 12 },
      { index: 13, name: '을지로4가역', id: 13, x: 13, y: 13 },
      { index: 14, name: '을지로5가역', id: 14, x: 14, y: 14 },
      { index: 15, name: '을지로6가역', id: 15, x: 15, y: 15 },
    ],
  },
};
