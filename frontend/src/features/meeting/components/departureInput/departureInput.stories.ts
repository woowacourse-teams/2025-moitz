import DepartureInput from './DepartureInput';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'features/meeting/DepartureInput',
  component: DepartureInput,
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
  // TODO : decorator로 withLayout 추가 예정
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
