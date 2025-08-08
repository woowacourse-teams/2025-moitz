import { routeCardMock } from '@mocks/routeCardMock';

import { withContainer } from '../../../../../.storybook/decorators/withContainer';

import RouteCardDetail from './RouteCardDetail';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/detail/RouteCardDetail',
  component: RouteCardDetail,
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
} satisfies Meta<typeof RouteCardDetail>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    paths: routeCardMock.paths,
  },
};
