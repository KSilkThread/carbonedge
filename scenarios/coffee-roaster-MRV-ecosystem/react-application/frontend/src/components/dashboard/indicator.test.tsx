import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, fireEvent, screen } from "@testing-library/react";
import Indicator from "./indicator";
import "@testing-library/jest-dom";

vi.mock("../../hooks/usePost", () => {
  const originalModule = vi.importActual("../../hooks/usePost");
  return {
    __esModule: true,
    ...originalModule,
    default: () => ({
      data: null,
      error: null,
      loading: false,
      post: vi.fn().mockImplementation(() => Promise.resolve()),
    }),
  };
});

describe("Indicator", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("renders loading state correctly", () => {
    render(<Indicator loading={true} error={null} data={null} />);
    expect(screen.getByRole("progressbar")).toBeInTheDocument();
  });

  it("renders error state correctly", () => {
    render(<Indicator loading={false} error={true} data={null} />);
    expect(screen.getByText(/Error Fetching/i)).toBeInTheDocument();
    expect(screen.getByText(/See Console/i)).toBeInTheDocument();
  });

  it("renders data with future expiry date correctly", () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 15);
    const data = {
      response: JSON.stringify({ expirydate: futureDate.toISOString() }),
    };
    render(<Indicator loading={false} error={null} data={data} />);
    expect(screen.getByText(/Calibrated/i)).toBeInTheDocument();
  });

  it("renders data with past expiry date correctly", () => {
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 1);
    const data = {
      response: JSON.stringify({ expirydate: pastDate.toISOString() }),
    };
    render(<Indicator loading={false} error={null} data={data} />);
    expect(screen.getByText(/Expired/i)).toBeInTheDocument();
  });

  it("starts and completes calibration process", async () => {
    const { getByText } = render(
      <Indicator loading={false} error={null} data={null} login="inspector" />
    );
    fireEvent.mouseDown(getByText("Calibrate"));
    vi.advanceTimersByTime(3000);
    fireEvent.mouseUp(getByText("Calibrate"));
    expect(getByText("Finished")).toBeInTheDocument();
  });
});
