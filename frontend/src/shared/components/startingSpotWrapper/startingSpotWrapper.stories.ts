import startSpotNameListMock from '../../../mocks/startingSpotNameListMock';

import StartingSpotWrapper from './StartingSpotWrapper';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/StartingSpotWrapper',
  component: StartingSpotWrapper,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    startingSpotNameList: {
      control: { type: 'object' },
      description: '출발지 이름 목록',
    },
  },
} satisfies Meta<typeof StartingSpotWrapper>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    startingSpotNameList: startSpotNameListMock,
  },
};

export const Long: Story = {
  args: {
    startingSpotNameList: [
      { index: 0, name: '서울역' },
      { index: 1, name: '강남역' },
      { index: 2, name: '역삼역' },
      { index: 3, name: '잠실역' },
      { index: 4, name: '홍대입구역' },
      { index: 5, name: '신촌역' },
      { index: 6, name: '을지로입구역' },
      { index: 7, name: '종각역' },
      { index: 8, name: '시청역' },
      { index: 9, name: '광화문역' },
      { index: 10, name: '안국역' },
      { index: 11, name: '종로3가역' },
      { index: 12, name: '을지로3가역' },
      { index: 13, name: '을지로4가역' },
      { index: 14, name: '을지로5가역' },
      { index: 15, name: '을지로6가역' },
    ],
  },
};
