import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import { MemoryRouter } from "react-router-dom";
import { LoginProvider } from "../../context/LoginContext";
import Dashboard from "./dashboard";

vi.mock("../../hooks/useFetch", () => ({
  __esModule: true,
  default: () => ({
    data: null,
    loading: false,
    error: null,
  }),
}));

vi.mock("../../context/LoginContext", () => ({
  __esModule: true,
  useLogin: () => ({
    loginStatus: "owner",
  }),
  LoginProvider: ({ children }: any) => <div>{children}</div>,
}));

describe("Dashboard", () => {
  it("renders without crashing", () => {
    render(
      <MemoryRouter>
        <LoginProvider>
          <Dashboard />
        </LoginProvider>
      </MemoryRouter>
    );
    expect(screen.getByText("Refresh")).toBeInTheDocument();
  });
});
