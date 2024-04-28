# Carbon Edge Dashboard - React Application

## Installation

1. Clone this git repository
2. cd into the frontend folder with: `cd frontend`
3. run `npm install`
4. copy the example .env file to a real .env file with `cp .env.example .env`
5. configure all values in the .env file as needed. (The Sensor ID must be same as in the Blockchain, so does the Organisation ID)
6. run `npm run dev` for a local environment with Hot Module Replacement (HMR)

## Testing

### Unit Tests

Run `npm run test`, it will automatically include a coverage report for unit tests

### Storybook

Run `npm run storybook` it opens storybook in the browser to manually test the components

### Visual Regression Testing

Install and open docker (desktop)

Run `npx loki test` to test changes against the reference images created by loki.

# Default Readme for Reference

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react/README.md) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type aware lint rules:

- Configure the top-level `parserOptions` property like this:

```js
export default {
  // other rules...
  parserOptions: {
    ecmaVersion: "latest",
    sourceType: "module",
    project: ["./tsconfig.json", "./tsconfig.node.json"],
    tsconfigRootDir: __dirname,
  },
};
```

- Replace `plugin:@typescript-eslint/recommended` to `plugin:@typescript-eslint/recommended-type-checked` or `plugin:@typescript-eslint/strict-type-checked`
- Optionally add `plugin:@typescript-eslint/stylistic-type-checked`
- Install [eslint-plugin-react](https://github.com/jsx-eslint/eslint-plugin-react) and add `plugin:react/recommended` & `plugin:react/jsx-runtime` to the `extends` list
