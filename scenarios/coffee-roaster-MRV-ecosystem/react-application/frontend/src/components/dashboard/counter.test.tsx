import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import Counter from "./counter";
import "@testing-library/jest-dom";

describe("Counter", () => {
  it("renders loading state correctly", () => {
    render(
      <Counter
        headline="Test Headline"
        fieldname="testField"
        data={{}}
        error={null}
        loading={true}
        unit="kg"
        fn={(x) => x}
      />
    );
    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  it("renders error state correctly", () => {
    const errorMessage = "Test error";
    render(
      <Counter
        headline="Test Headline"
        fieldname="testField"
        data={{}}
        error={{ message: errorMessage }}
        loading={false}
        unit="kg"
        fn={(x) => x}
      />
    );
    expect(screen.getByText(`Error: ${errorMessage}`)).toBeInTheDocument();
  });

  it("renders data correctly", () => {
    const testValue = 100;
    const mockFn = vi.fn().mockReturnValue(testValue);
    const testData = { testField: 100 };
    render(
      <Counter
        headline="Test Headline"
        fieldname="testField"
        data={testData}
        error={null}
        loading={false}
        unit="kg"
        fn={mockFn}
      />
    );
    expect(screen.getByText(`${testValue} kg`)).toBeInTheDocument();
    expect(mockFn).toHaveBeenCalledWith(testData.testField);
  });
});
