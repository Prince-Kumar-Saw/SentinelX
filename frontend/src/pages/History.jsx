import { useState, useEffect } from 'react'
import axios from 'axios'

function History({ token }) {
  const [scans, setScans] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchHistory()
  }, [])

  const fetchHistory = async () => {
    try {
      const response = await axios.get(
        'http://localhost:8080/api/scan/history',
        { headers: { Authorization: `Bearer ${token}` } }
      )
      setScans(response.data)
    } catch (err) {
      setError('Failed to load scan history')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this scan?')) return
    try {
      await axios.delete(
        `http://localhost:8080/api/scan/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      )
      setScans(scans.filter(scan => scan.id !== id))
    } catch (err) {
      setError('Failed to delete scan')
    }
  }

  const getRiskBadge = (level) => {
    switch (level) {
      case 'SAFE': return 'bg-green-900 text-green-300 border border-green-700'
      case 'LOW': return 'bg-yellow-900 text-yellow-300 border border-yellow-700'
      case 'MEDIUM': return 'bg-orange-900 text-orange-300 border border-orange-700'
      case 'HIGH': return 'bg-red-900 text-red-300 border border-red-700'
      default: return 'bg-gray-800 text-gray-300 border border-gray-700'
    }
  }

  const getRiskEmoji = (level) => {
    switch (level) {
      case 'SAFE': return '✅'
      case 'LOW': return '⚠️'
      case 'MEDIUM': return '🔶'
      case 'HIGH': return '🚨'
      default: return '🔍'
    }
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <p className="text-gray-400 text-lg">Loading scan history...</p>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-950 p-6">
      <div className="max-w-6xl mx-auto">

        {/* Header */}
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-white">📋 Scan History</h2>
            <p className="text-gray-400 mt-1">All your previous URL scans</p>
          </div>
          <div className="bg-gray-900 border border-gray-800 rounded-xl px-4 py-2">
            <p className="text-gray-400 text-sm">Total Scans</p>
            <p className="text-white text-xl font-bold text-center">{scans.length}</p>
          </div>
        </div>

        {/* Error */}
        {error && (
          <div className="bg-red-900 border border-red-700 text-red-300 px-4 py-3 rounded-lg mb-6">
            {error}
          </div>
        )}

        {/* Empty State */}
        {scans.length === 0 && !error && (
          <div className="bg-gray-900 rounded-2xl p-12 border border-gray-800 text-center">
            <p className="text-4xl mb-4">🔍</p>
            <p className="text-white text-lg font-semibold">No scans yet!</p>
            <p className="text-gray-400 mt-2">Go to URL Scanner and scan your first URL</p>
          </div>
        )}

        {/* Scans Table */}
        {scans.length > 0 && (
          <div className="bg-gray-900 rounded-2xl border border-gray-800 overflow-hidden">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-800">
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">#</th>
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">URL</th>
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">Risk Score</th>
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">Level</th>
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">Engines</th>
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">Scanned At</th>
                  <th className="text-left text-gray-400 text-sm font-medium px-6 py-4">Action</th>
                </tr>
              </thead>
              <tbody>
                {scans.map((scan, index) => (
                  <tr
                    key={scan.id}
                    className="border-b border-gray-800 hover:bg-gray-800 transition duration-150"
                  >
                    <td className="px-6 py-4 text-gray-500 text-sm">{index + 1}</td>
                    <td className="px-6 py-4">
                      <p className="text-white text-sm truncate max-w-xs">{scan.url}</p>
                    </td>
                    <td className="px-6 py-4">
                      <p className="text-white font-semibold">{scan.riskScore}%</p>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`text-xs font-medium px-3 py-1 rounded-full ${getRiskBadge(scan.riskLevel)}`}>
                        {getRiskEmoji(scan.riskLevel)} {scan.riskLevel}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <p className="text-gray-400 text-sm">{scan.maliciousCount}/{scan.totalEngines}</p>
                    </td>
                    <td className="px-6 py-4">
                      <p className="text-gray-400 text-sm">{formatDate(scan.scannedAt)}</p>
                    </td>
                    <td className="px-6 py-4">
                      <button
                        onClick={() => handleDelete(scan.id)}
                        className="bg-red-900 hover:bg-red-800 text-red-300 border border-red-700 text-xs px-3 py-1 rounded-lg transition duration-200"
                      >
                        🗑️ Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
export default History