import api from "@/shared/lib/api"
import type { AppRole } from "@/shared/lib/auth"

export type LoginPayload = {
  email: string
  password: string
}

export type StaffLoginResponse = {
  token: string
  email: string
  role: AppRole
}

export async function loginSystem(payload: LoginPayload) {
  const { data } = await api.post<StaffLoginResponse>("/api/auth/login", payload)
  return data
}
