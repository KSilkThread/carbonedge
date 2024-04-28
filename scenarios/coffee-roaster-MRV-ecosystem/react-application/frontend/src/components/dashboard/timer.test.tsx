import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Timer from "./timer";

describe("Timer Component", () => {
  it("displays loader when loading", () => {
    render(<Timer loading={true} error={null} data={null} />);
    const loader = screen.getByRole("progressbar");
    expect(loader).toBeInTheDocument();
  });

  it("displays error message when there is an error", () => {
    render(<Timer loading={false} error={true} data={null} />);
    const errorMessage = screen.getByText("Error loading data");
    expect(errorMessage).toBeInTheDocument();
  });

  it("displays time until expiry when data is provided", () => {
    const fixedDate = new Date("2024-01-01T00:00:00Z");
    vi.useFakeTimers().setSystemTime(fixedDate);

    const mockData = {
      response: JSON.stringify({
        expirydate: new Date(
          fixedDate.getTime() + 5 * 24 * 60 * 60 * 1000
        ).toISOString(),
      }),
    };
    render(<Timer loading={false} error={null} data={mockData} />);
    const expiryMessage = screen.getByText(/days/);
    expect(expiryMessage).toBeInTheDocument();
    expect(expiryMessage).toHaveTextContent("5 days");

    vi.useRealTimers();
  });

  it("displays 'No Data' when no expiry date is provided", () => {
    const mockData = {
      response: JSON.stringify({}),
    };
    render(<Timer loading={false} error={null} data={mockData} />);
    const naMessage = screen.getByText("No Data");
    expect(naMessage).toBeInTheDocument();
  });
});
