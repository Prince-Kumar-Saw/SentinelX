import { useState } from 'react'
import Login from './pages/Login'
import Register from './pages/Register'
import Scanner from './pages/Scanner'

function App() {
  const [user, setUser] = useState(null)
  const [page, setPage] = useState('login')
  const [activePage, setActivePage] = useState('scanner')

  const handleLogin = (data) => {
    setUser(data)
    localStorage.setItem('token', data.token)
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('token')
    setPage('login')
  }

  // Show auth pages if not logged in
  if (!user) {
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

  // Main app layout after login
  return (
    <div className="min-h-screen bg-gray-950 flex">

      {/* Sidebar */}
      <div className="w-64 bg-gray-900 border-r border-gray-800 flex flex-col">

        {/* Logo */}
        <div className="p-6 border-b border-gray-800">
          <h1 className="text-xl font-bold text-white">🛡️ SentinelX</h1>
          <p className="text-gray-500 text-xs mt-1">Cyber Threat Intelligence</p>
        </div>

        {/* Nav Links */}
        <nav className="flex-1 p-4">
          <button
            onClick={() => setActivePage('scanner')}
            className={`w-full text-left px-4 py-3 rounded-lg mb-2 transition duration-200 ${
              activePage === 'scanner'
                ? 'bg-blue-600 text-white'
                : 'text-gray-400 hover:bg-gray-800 hover:text-white'
            }`}
          >
            🔍 URL Scanner
          </button>
        </nav>

        {/* User Info */}
        <div className="p-4 border-t border-gray-800">
          <p className="text-white text-sm font-semibold truncate">{user.name}</p>
          <p className="text-gray-500 text-xs truncate">{user.email}</p>
          <p className="text-blue-400 text-xs mt-1">{user.role}</p>
          <button
            onClick={handleLogout}
            className="mt-3 w-full bg-red-600 hover:bg-red-700 text-white text-sm py-2 rounded-lg transition duration-200"
          >
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">
        {activePage === 'scanner' && <Scanner token={user.token} />}
      </div>

    </div>
  )
}

export default App
