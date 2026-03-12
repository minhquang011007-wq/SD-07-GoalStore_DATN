const API_BASE_URL = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/$/, "")

export function resolveProductImageUrl(url?: string | null) {
  if (!url) return ""
  if (/^https?:\/\//i.test(url)) return url
  return `${API_BASE_URL}${url.startsWith("/") ? "" : "/"}${url}`
}
