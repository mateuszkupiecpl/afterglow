import { Navigate, Route, Routes } from 'react-router-dom'
import { SessionPage } from '../pages/SessionPage'

function App() {
  return (
    <Routes>
      <Route path="/" element={<SessionPage />} />
      <Route path="*" element={<Navigate replace to="/" />} />
    </Routes>
  )
}

export default App
