import { routeCardMock } from '@mocks/routeCardMock';

import { withContainer } from '@sb/decorators/withContainer';

import RouteCardBar from './RouteCardBar';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/detail/RouteCardBar',
  component: RouteCardBar,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    paths: {
      control: { type: 'object' },
      description: '경로 정보',
    },
  },
} satisfies Meta<typeof RouteCardBar>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    paths: routeCardMock.paths,
  },
};
