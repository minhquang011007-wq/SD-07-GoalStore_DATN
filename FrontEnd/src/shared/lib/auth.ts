export type AppRole = "ADMIN" | "SALES" | "INVENTORY" | "CUSTOMER"

export type AuthSession = {
  token: string
  role: AppRole
  username?: string
  email?: string
  displayName?: string
  customerId?: number | string
}

const AUTH_KEYS = ["token", "role", "username", "email", "displayName", "customerId"] as const

export function saveSession(session: AuthSession) {
  localStorage.setItem("token", session.token)
  localStorage.setItem("role", session.role)

  if (session.username) localStorage.setItem("username", session.username)
  else localStorage.removeItem("username")

  if (session.email) localStorage.setItem("email", session.email)
  else localStorage.removeItem("email")

  if (session.displayName) localStorage.setItem("displayName", session.displayName)
  else localStorage.removeItem("displayName")

  if (session.customerId !== undefined && session.customerId !== null) {
    localStorage.setItem("customerId", String(session.customerId))
  } else {
    localStorage.removeItem("customerId")
  }
}

export function clearSession() {
  AUTH_KEYS.forEach((key) => localStorage.removeItem(key))
}

export function getRole(): AppRole | "" {
  return (localStorage.getItem("role") || "") as AppRole | ""
}

export function isLoggedIn() {
  return Boolean(localStorage.getItem("token"))
}

export function getDisplayName() {
  return (
    localStorage.getItem("displayName") ||
    localStorage.getItem("username") ||
    localStorage.getItem("email") ||
    "Tài khoản"
  )
}
