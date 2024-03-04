/* c8 ignore next 100 */

import type { Meta, StoryObj } from "@storybook/react";
import Timer from "../components/dashboard/timer";

const meta: Meta<typeof Timer> = {
  component: Timer,
};

export default meta;
type Story = StoryObj<typeof Timer>;

export const Primary: Story = {
  args: {
    loading: false,
    data: { response: JSON.stringify({ expirydate: "2024-09-15T12:00:00Z" }) },
    error: null,
  },
  render: ({ loading, data, error }) => (
    <Timer loading={loading} data={data} error={error} />
  ),
};
