import { withLayout } from '../../.storybook/decorators/withLayout';
import App from '../app/App';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof App> = {
  component: App,
  decorators: [withLayout],
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof App>;

export const Index: Story = {
  parameters: {
    pathname: '/',
  },
};

export const Result: Story = {
  parameters: {
    pathname: '/result',
  },
};

export const NotFound: Story = {
  parameters: {
    pathname: '/NotFound',
  },
};
