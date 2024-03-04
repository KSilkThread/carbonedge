/* c8 ignore next 100 */

import type { Meta, StoryObj } from "@storybook/react";
import Counter from "../components/dashboard/counter";

const meta: Meta<typeof Counter> = {
  component: Counter,
};

export default meta;
type Story = StoryObj<typeof Counter>;

export const Primary: Story = {
  args: {
    headline: "Batch CO2 Emissions",
    fieldname: "CO2_batch",
    loading: false,
    data: {
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
    error: null,
    unit: "kg",
    fn: (x) => Math.round(x / 1000),
  },
  render: ({ loading, data, error, headline, fieldname, fn, unit }) => (
    <Counter
      headline={headline}
      fieldname={fieldname}
      loading={loading}
      data={data}
      error={error}
      unit={unit}
      fn={fn}
    />
  ),
};
