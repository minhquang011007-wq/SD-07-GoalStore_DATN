export type AppRole = 'ADMIN' | 'SALES' | 'INVENTORY' | 'CUSTOMER'

export type AuthSession = {
  token: string
  role: AppRole
  email?: string
  displayName?: string
  customerId?: number | string
}

export function saveSession(payload: AuthSession) {
  localStorage.setItem('token', payload.token)
  localStorage.setItem('role', payload.role)
  if (payload.email) localStorage.setItem('email', payload.email)
  else localStorage.removeItem('email')
  if (payload.displayName) localStorage.setItem('displayName', payload.displayName)
  else localStorage.removeItem('displayName')
  if (payload.customerId !== undefined && payload.customerId !== null) {
    localStorage.setItem('customerId', String(payload.customerId))
  } else {
    localStorage.removeItem('customerId')
  }
}

export function clearSession() {
  ;['token', 'role', 'email', 'displayName', 'customerId'].forEach((key) => localStorage.removeItem(key))
}

export function getRole(): AppRole | '' {
  return (localStorage.getItem('role') || '') as AppRole | ''
}

export function isLoggedIn() {
  return Boolean(localStorage.getItem('token'))
}

export function isCustomerLoggedIn() {
  return getRole() === 'CUSTOMER' && Boolean(localStorage.getItem('token'))
}

export function getCustomerId() {
  const raw = localStorage.getItem('customerId')
  if (!raw) return null
  const parsed = Number(raw)
  return Number.isNaN(parsed) ? raw : parsed
}

export function getDisplayName() {
  return localStorage.getItem('displayName') || localStorage.getItem('email') || 'Khách hàng'
}
