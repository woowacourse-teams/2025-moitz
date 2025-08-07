import { routeCardMock } from '@mocks/routeCardMock';

import { withContainer } from '../../../../../.storybook/decorators/withContainer';

import RouteCard from './RouteCard';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/detail/RouteCard',
  component: RouteCard,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
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
    startingPlaceName: '선릉역',
    route: routeCardMock,
  },
};
