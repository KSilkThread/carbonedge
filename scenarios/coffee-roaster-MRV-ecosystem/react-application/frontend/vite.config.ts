import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  preview: {
    host: true,
    port: 8080
  },
   test: {
    globals: true,
    environment: 'jsdom'
  }, 
});
