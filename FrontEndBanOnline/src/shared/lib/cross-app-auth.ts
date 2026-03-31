import { saveSession, type AppRole } from "@/shared/lib/auth"

export function buildCrossAppUrl(
  baseUrl: string,
  session: {
    token: string
    role: AppRole
    email?: string
    displayName?: string
    customerId?: number | string
  },
  path = "/"
) {
  const url = new URL(path, baseUrl)
  url.searchParams.set("token", session.token)
  url.searchParams.set("role", session.role)
  if (session.email) url.searchParams.set("email", session.email)
  if (session.displayName) url.searchParams.set("displayName", session.displayName)
  if (session.customerId !== undefined && session.customerId !== null) {
    url.searchParams.set("customerId", String(session.customerId))
  }
  return url.toString()
}

export function hydrateSessionFromUrl() {
  const url = new URL(window.location.href)
  const token = url.searchParams.get("token")
  const role = url.searchParams.get("role") as AppRole | null

  if (!token || !role) return false

  saveSession({
    token,
    role,
    email: url.searchParams.get("email") || undefined,
    displayName: url.searchParams.get("displayName") || undefined,
    customerId: url.searchParams.get("customerId") || undefined,
  })

  ;["token", "role", "email", "displayName", "customerId"].forEach((key) => {
    url.searchParams.delete(key)
  })

  window.history.replaceState({}, document.title, `${url.pathname}${url.search}${url.hash}`)
  return true
}
