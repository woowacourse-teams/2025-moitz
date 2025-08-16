import { withContainer } from '@sb/decorators/withContainer';

import Dropdown from './Dropdown';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: Dropdown,
  decorators: [withContainer],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    stations: {
      control: { type: 'object' },
      description: '역 목록',
    },
    handleStationSelect: {
      description: '역 선택 시 실행될 함수',
    },
  },
} satisfies Meta<typeof Dropdown>;

export default meta;
type Story = StoryObj<typeof meta>;

export const List: Story = {
  args: {
    stations: ['서울역', '강남역', '역삼역'],
    handleStationSelect: () => {},
  },
};

export const Empty: Story = {
  args: {
    stations: [],
    handleStationSelect: () => {},
  },
};
