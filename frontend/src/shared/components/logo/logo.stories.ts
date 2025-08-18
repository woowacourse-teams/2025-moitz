import Logo from './Logo';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  component: Logo,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: 'select',
      options: ['black', 'white'],
    },
  },
} satisfies Meta<typeof Logo>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    type: 'black',
  },
};

export const White: Story = {
  args: {
    type: 'white',
  },
};
