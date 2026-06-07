import { useState } from 'react'
import Login from './pages/Login'
import Register from './pages/Register'

function App() {
  const [user, setUser] = useState(null)
  const [page, setPage] = useState('login')

  const handleLogin = (data) => {
    setUser(data)
    localStorage.setItem('token', data.token)
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('token')
  }

  // If logged in show dashboard (temporary)
  if (user) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="bg-gray-900 rounded-2xl p-8 w-full max-w-md border border-gray-800 text-center">
          <h1 className="text-3xl font-bold text-white mb-2">🛡️ SentinelX</h1>
          <p className="text-green-400 text-lg font-semibold mb-1">✅ Welcome, {user.name}!</p>
          <p className="text-gray-400 mb-2">Email: {user.email}</p>
          <p className="text-gray-400 mb-6">Role: <span className="text-blue-400">{user.role}</span></p>
          <button
            onClick={handleLogout}
            className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-6 rounded-lg transition duration-200"
          >
            Logout
          </button>
        </div>
      </div>
    )
  }

  // Show login or register
  if (page === 'login') {
    return (
      <Login
        onLogin={handleLogin}
        switchToRegister={() => setPage('register')}
      />
    )
  }

  return (
    <Register
      onLogin={handleLogin}
      switchToLogin={() => setPage('login')}
    />
  )
}

export default App