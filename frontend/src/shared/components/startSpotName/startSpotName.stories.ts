import startSpotNameListMock from 'src/mocks/startSpotNameListMock';

import StartSpotName from './startSpotName';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'shared/StartSpotName',
  component: StartSpotName,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    nameInfo: {
      control: { type: 'object' },
      description: '출발지 이름 목록',
    },
  },
} satisfies Meta<typeof StartSpotName>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    nameInfo: startSpotNameListMock[0],
  },
};
