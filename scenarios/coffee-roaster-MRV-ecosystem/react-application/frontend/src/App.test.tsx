import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { ThemeProvider } from "@emotion/react";
import theme from "./themes/default";
import { LoginProvider } from "./context/LoginContext";
import App from "./App";
import "@testing-library/jest-dom";

describe("App Component", () => {
  test("renders Login component when path is /", () => {
    render(
      <ThemeProvider theme={theme}>
        <LoginProvider>
          <MemoryRouter initialEntries={["/"]}>
            <Routes>
              <Route path="/" element={<App />} />
            </Routes>
          </MemoryRouter>
        </LoginProvider>
      </ThemeProvider>
    );

    expect(
      screen.getByText(/Please insert your credentials chip/i)
    ).toBeInTheDocument();
  });
});
