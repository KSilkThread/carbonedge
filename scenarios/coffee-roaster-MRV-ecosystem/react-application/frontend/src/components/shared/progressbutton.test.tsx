import { describe, it, expect } from "vitest";
import { render, RenderResult } from "@testing-library/react";
import ProgressButton from "./progressbutton";
import "@testing-library/jest-dom";

describe("ProgressButton", () => {
  it("renders with correct width based on value, min, and max", () => {
    const { container }: RenderResult = render(
      <ProgressButton value={50} min={0} max={100} />
    );
    const progressBar: HTMLElement | null =
      container.querySelector(".button__progress");
    expect(progressBar).not.toBeNull();
    expect(progressBar).toHaveStyle("width: 50%");
  });

  it("renders correctly when value is equal to min", () => {
    const { container }: RenderResult = render(
      <ProgressButton value={0} min={0} max={100} />
    );
    const progressBar: HTMLElement | null =
      container.querySelector(".button__progress");
    expect(progressBar).not.toBeNull();
    expect(progressBar).toHaveStyle("width: 0%");
  });

  it("renders correctly when value is equal to max", () => {
    const { container }: RenderResult = render(
      <ProgressButton value={100} min={0} max={100} />
    );
    const progressBar: HTMLElement | null =
      container.querySelector(".button__progress");
    expect(progressBar).not.toBeNull();
    expect(progressBar).toHaveStyle("width: 100%");
  });
});
