/* c8 ignore next 100 */

import type { Meta, StoryObj } from "@storybook/react";
import { DashboardView } from "../components/dashboard/dashboard";

const meta: Meta<typeof DashboardView> = {
  component: DashboardView,
};

export default meta;
type Story = StoryObj<typeof DashboardView>;

export const Primary: Story = {
  args: {
    refresh: () => window.location.reload(),
    data: { response: JSON.stringify({ expirydate: "2024-09-15T12:00:00Z" }) },
    loading: false,
    error: null,
    emissionData: {
      BTU_batch: 239123.388,
      BTU_batch_per_green_kg: 11956.169,
      CO2_batch: 15999.225,
      BTU_preheat: 209123.388,
      CO2_preheat: 13999,
      BTU_bbp: 97000,
      CO2_bbp: 7000,
      BTU_cooling: 17000,
      CO2_cooling: 2000,
      BTU_roast: 239123.388,
      BTU_roast_per_green_kg: 11956.169,
      CO2_roast: 15999.225,
      CO2_batch_per_green_kg: 799.961,
      CO2_roast_per_green_kg: 799.961,
      BTU_LPG: 234998.917,
      BTU_NG: 0,
      BTU_ELEC: 4124.471,
      KWH_batch_per_green_kg: 3.504,
      KWH_roast_per_green_kg: 3.504,
    },
    emissionLoading: false,
    emissionError: null,
    loginStatus: "inspector",
  },
  render: ({
    loading,
    data,
    error,
    loginStatus,
    emissionData,
    emissionError,
    emissionLoading,
    refresh,
  }) => (
    <DashboardView
      refresh={refresh}
      data={data}
      loading={loading}
      error={error}
      emissionData={emissionData}
      emissionLoading={emissionLoading}
      emissionError={emissionError}
      loginStatus={loginStatus}
    />
  ),
};
