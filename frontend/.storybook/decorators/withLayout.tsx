import { Decorator } from '@storybook/react-webpack5';

import Layout from '@shared/components/layout/Layout';

export const withLayout: Decorator = (Story) => (
  <Layout>
    <Story />
  </Layout>
);
