import { routeCardMock } from '@mocks/routeCardMock';

import { withContainer } from '@sb/decorators/withContainer';

import RouteCard from './RouteCard';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: RouteCard,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    startingPlaceIndex: {
      control: { type: 'text' },
      description: '출발지 인덱스',
    },
    startingPlaceName: {
      control: { type: 'text' },
      description: '출발지 이름',
    },
    route: {
      control: { type: 'object' },
      description: '경로 정보',
    },
  },
} satisfies Meta<typeof RouteCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    startingPlaceIndex: 'A',
    startingPlaceName: '선릉역',
    route: routeCardMock,
  },
};
