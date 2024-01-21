import { Route, Routes } from 'react-router-dom'
import Login from './components/login/login'
import Dashboard from './components/dashboard/dashboard'
import { ThemeProvider } from '@emotion/react'
import theme from './themes/default'

function App() {
  return (
    <>
    <ThemeProvider theme={theme}>
      <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
    </ThemeProvider>

    </>
  )
}

export default App
