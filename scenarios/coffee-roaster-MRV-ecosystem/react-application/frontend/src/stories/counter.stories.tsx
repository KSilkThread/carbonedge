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
    data: { CO2_Batch: 2125.12441 },
    error: null,
    unit: "kg",
    fn: (x) => Math.round(x / 1000),
  },
  render: () => (
    <Counter
      headline={"Batch CO2 Emissions"}
      fieldname={"CO2_batch"}
      loading={false}
      data={{ CO2_Batch: 2125.12441 }}
      error={null}
      unit={"kg"}
      fn={(x) => Math.round(x / 1000)}
    />
  ),
};
