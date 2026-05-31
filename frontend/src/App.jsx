import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [health, setHealth] = useState(null)

  useEffect(() => {
    axios.get('http://localhost:8080/api/health')
      .then(response => setHealth(response.data))
      .catch(error => console.log(error))
  }, [])

  return (
    <div>
      <h1>SentinelX 🛡️</h1>
      <h2>AI Cyber Threat Intelligence Platform</h2>
      {health ? (
        <p style={{color: 'green'}}>✅ Backend Status: {health.status}</p>
      ) : (
        <p style={{color: 'red'}}>❌ Backend not connected</p>
      )}
    </div>
  )
}

export default App