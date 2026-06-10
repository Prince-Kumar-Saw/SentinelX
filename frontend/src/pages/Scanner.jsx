import { useState } from 'react'
import axios from 'axios'

function Scanner({ token }) {
  const [url, setUrl] = useState('')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')

  const handleScan = async () => {
    if (!url) {
      setError('Please enter a URL')
      return
    }
    setLoading(true)
    setError('')
    setResult(null)

    try {
      const response = await axios.post(
        'http://localhost:8080/api/scan/url',
        { url },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      setResult(response.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Scan failed!')
    } finally {
      setLoading(false)
    }
  }

  const getRiskColor = (level) => {
    switch (level) {
      case 'SAFE': return 'text-green-400'
      case 'LOW': return 'text-yellow-400'
      case 'MEDIUM': return 'text-orange-400'
      case 'HIGH': return 'text-red-400'
      default: return 'text-gray-400'
    }
  }

  const getRiskBg = (level) => {
    switch (level) {
      case 'SAFE': return 'bg-green-900 border-green-700'
      case 'LOW': return 'bg-yellow-900 border-yellow-700'
      case 'MEDIUM': return 'bg-orange-900 border-orange-700'
      case 'HIGH': return 'bg-red-900 border-red-700'
      default: return 'bg-gray-800 border-gray-700'
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

  return (
    <div className="min-h-screen bg-gray-950 p-6">
      <div className="max-w-2xl mx-auto">

        {/* Header */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-white">🔍 URL Scanner</h2>
          <p className="text-gray-400 mt-1">Check if a URL is safe or malicious</p>
        </div>

        {/* Scan Input */}
        <div className="bg-gray-900 rounded-2xl p-6 border border-gray-800 mb-6">
          <label className="text-gray-400 text-sm mb-2 block">Enter URL to scan</label>
          <div className="flex gap-3">
            <input
              type="text"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleScan()}
              className="flex-1 bg-gray-800 text-white border border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:border-blue-500"
              placeholder="https://example.com"
            />
            <button
              onClick={handleScan}
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-3 rounded-lg transition duration-200 disabled:opacity-50 whitespace-nowrap"
            >
              {loading ? 'Scanning...' : 'Scan URL'}
            </button>
          </div>

          {/* Error */}
          {error && (
            <div className="mt-3 bg-red-900 border border-red-700 text-red-300 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}
        </div>

        {/* Loading */}
        {loading && (
          <div className="bg-gray-900 rounded-2xl p-8 border border-gray-800 text-center">
            <div className="text-4xl mb-4">🔍</div>
            <p className="text-white font-semibold text-lg">Scanning URL...</p>
            <p className="text-gray-400 mt-2">Checking against 90+ threat engines</p>
            <p className="text-gray-500 text-sm mt-1">This may take 15-30 seconds</p>
          </div>
        )}

        {/* Results */}
        {result && !loading && (
          <div className={`rounded-2xl p-6 border ${getRiskBg(result.riskLevel)} mb-6`}>

            {/* Risk Level Header */}
            <div className="flex items-center justify-between mb-6">
              <div>
                <p className="text-gray-400 text-sm">Threat Assessment</p>
                <p className={`text-3xl font-bold ${getRiskColor(result.riskLevel)}`}>
                  {getRiskEmoji(result.riskLevel)} {result.riskLevel}
                </p>
              </div>
              <div className="text-right">
                <p className="text-gray-400 text-sm">Risk Score</p>
                <p className={`text-4xl font-bold ${getRiskColor(result.riskLevel)}`}>
                  {result.riskScore}%
                </p>
              </div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="bg-gray-900 bg-opacity-50 rounded-xl p-4">
                <p className="text-gray-400 text-sm">Malicious Engines</p>
                <p className="text-red-400 text-2xl font-bold">{result.maliciousCount}</p>
              </div>
              <div className="bg-gray-900 bg-opacity-50 rounded-xl p-4">
                <p className="text-gray-400 text-sm">Total Engines</p>
                <p className="text-white text-2xl font-bold">{result.totalEngines}</p>
              </div>
            </div>

            {/* URL */}
            <div className="bg-gray-900 bg-opacity-50 rounded-xl p-4 mb-4">
              <p className="text-gray-400 text-sm mb-1">Scanned URL</p>
              <p className="text-white text-sm break-all">{result.url}</p>
            </div>

            {/* AI Explanation */}
            <div className="bg-gray-900 bg-opacity-50 rounded-xl p-4">
              <p className="text-gray-400 text-sm mb-1">Analysis</p>
              <p className="text-white text-sm">{result.aiExplanation}</p>
            </div>

          </div>
        )}

      </div>
    </div>
  )
}

export default Scanner