const API_URL = (import.meta.env.VITE_API_URL as string | undefined) || "http://localhost:8080"

type FetchOptions = {
  method?: string
  body?: unknown
  headers?: Record<string, string>
  skipAuth?: boolean
}

export async function apiRequest<T>(path: string, options: FetchOptions = {}) {
  const token = localStorage.getItem("token")

  const response = await fetch(`${API_URL}${path}`, {
    method: options.method || "GET",
    headers: {
      "Content-Type": "application/json",
      ...(!options.skipAuth && token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  })

  if (!response.ok) {
    let message = "Yêu cầu thất bại"
    try {
      const errorData = await response.json()
      message = errorData?.message || errorData?.error || message
    } catch {
    }

    const error = new Error(message) as Error & { response?: { data?: { message: string } } }
    error.response = { data: { message } }
    throw error
  }

  return (await response.json()) as T
}