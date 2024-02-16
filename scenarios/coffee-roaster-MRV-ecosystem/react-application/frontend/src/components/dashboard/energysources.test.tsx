import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Energysources from "./energysources";

describe("Energysources Component", () => {
  const mockData = {
    BTU_batch: 100,
    BTU_batch_per_green_kg: 10,
    CO2_batch: 20,
    BTU_preheat: 30,
    CO2_preheat: 40,
    BTU_bbp: 50,
    CO2_bbp: 60,
    BTU_cooling: 70,
    CO2_cooling: 80,
    BTU_roast: 90,
    BTU_roast_per_green_kg: 100,
    CO2_roast: 110,
    CO2_batch_per_green_kg: 120,
    CO2_roast_per_green_kg: 130,
    BTU_LPG: 140,
    BTU_NG: 150,
    BTU_ELEC: 160,
    KWH_batch_per_green_kg: 170,
    KWH_roast_per_green_kg: 180,
  };

  it("displays loading text when loading", () => {
    render(<Energysources loading={true} error={null} data={null} />);
    const loadingText = screen.getByText("Loading...");
    expect(loadingText).toBeInTheDocument();
  });

  it("displays error message when there is an error", () => {
    render(
      <Energysources
        loading={false}
        error={{ message: "Network Error" }}
        data={null}
      />
    );
    const errorText = screen.getByText(/Error:/);
    expect(errorText).toBeInTheDocument();
    expect(errorText).toHaveTextContent("Network Error");
  });

  it("renders the chart with data", () => {
    render(<Energysources loading={false} error={null} data={mockData} />);
    const chartTitle = screen.getByText("Energy used (in BTU)");
    expect(chartTitle).toBeInTheDocument();
    const canvas = document.querySelector("canvas");
    expect(canvas).toBeInTheDocument();
    // Canvas kann nicht gecheckt werden, daher nur existenz des Titels
  });
});
