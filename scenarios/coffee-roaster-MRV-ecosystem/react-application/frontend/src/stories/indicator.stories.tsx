/* c8 ignore next 100 */

import type { Meta, StoryObj } from "@storybook/react";
import Indicator from "../components/dashboard/indicator";

const meta: Meta<typeof Indicator> = {
  component: Indicator,
};

export default meta;
type Story = StoryObj<typeof Indicator>;

export const Primary: Story = {
  args: {
    loading: false,
    data: { response: JSON.stringify({ expirydate: "2024-09-15T12:00:00Z" }) },
    error: null,
    login: "inspector",
  },
  render: ({ loading, data, error, login }) => (
    <Indicator login={login} loading={loading} data={data} error={error} />
  ),
};
