import { describe, it, expect, beforeEach } from "vitest";
import { render, waitFor, screen } from "@testing-library/react";
import { BrowserRouter as Router } from "react-router-dom";
import { LoginContext } from "../../context/LoginContext";
import Login from "./login";
import "@testing-library/jest-dom";

describe("Login Component", () => {
  beforeEach(() => {
    window.localStorage.clear();
  });
  it("should display the default message", async () => {
    render(
      <Router>
        <LoginContext.Provider
          value={{ loginStatus: "nobody", setLoginStatus: () => {} }}
        >
          <Login />
        </LoginContext.Provider>
      </Router>
    );
    await waitFor(() =>
      expect(
        screen.getByText(/Please insert your credentials chip/i)
      ).toBeInTheDocument()
    );
  });

  it("should display the success message for an owner", async () => {
    render(
      <Router>
        <LoginContext.Provider
          value={{ loginStatus: "owner", setLoginStatus: () => {} }}
        >
          <Login />
        </LoginContext.Provider>
      </Router>
    );
    await waitFor(() =>
      expect(screen.getByText(/Login as owner Successful/i)).toBeInTheDocument()
    );
  });

  it("should display the success message for an inspector", async () => {
    render(
      <Router>
        <LoginContext.Provider
          value={{ loginStatus: "inspector", setLoginStatus: () => {} }}
        >
          <Login />
        </LoginContext.Provider>
      </Router>
    );
    await waitFor(() =>
      expect(
        screen.getByText(/Login as inspector Successful/i)
      ).toBeInTheDocument()
    );
  });
});
