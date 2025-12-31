// Helper simple para centralizar llamadas fetch con Authorization
export async function apiFetch(path, options = {}) {
  const token = (typeof window !== 'undefined') ? localStorage.getItem('jwt') : null

  const headers = options.headers ? { ...options.headers } : {}

  // Si el body no es FormData y no hay Content-Type, asumimos JSON
  if (!(options.body instanceof FormData) && !headers['Content-Type'] && !headers['content-type']) {
    headers['Content-Type'] = 'application/json'
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const res = await fetch(path, { ...options, headers })

  if (!res.ok) {
    const text = await res.text()
    const err = new Error(`HTTP ${res.status}: ${text}`)
    err.status = res.status
    err.body = text
    throw err
  }

  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) return res.json()
  return res.text()
}
