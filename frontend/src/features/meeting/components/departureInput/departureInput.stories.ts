import { withLayout } from '@sb/decorators/withLayout';

import DepartureInput from './DepartureInput';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: DepartureInput,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    departureList: {
      control: { type: 'object' },
      description: '출발지 목록',
    },
    onAddDeparture: {
      description: '출발지 추가 시 실행될 함수',
    },
    onRemoveDeparture: {
      description: '출발지 삭제 시 실행될 함수',
    },
  },
} satisfies Meta<typeof DepartureInput>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    departureList: [],
    onAddDeparture: () => {},
    onRemoveDeparture: () => {},
  },
};
