import api from "@/shared/lib/api"
import type { AppRole } from "@/shared/lib/auth"

export type LoginPayload = {
  email: string
  password: string
}

export type StaffLoginResponse = {
  token: string
  email: string
  role: Exclude<AppRole, "CUSTOMER">
}

export type CustomerAuthResponse = {
  token: string
  customerId: number
  email: string
  ten: string
  role: "CUSTOMER"
}

export type CustomerRegisterPayload = {
  ten: string
  email: string
  password: string
  sdt?: string
  ngaySinh?: string | null
}

export async function loginSystem(payload: LoginPayload) {
  const { data } = await api.post<StaffLoginResponse>("/api/auth/login", payload)
  return data
}

export async function loginCustomer(payload: LoginPayload) {
  const { data } = await api.post<CustomerAuthResponse>("/api/customer-auth/login", payload)
  return data
}

export async function registerCustomer(payload: CustomerRegisterPayload) {
  const { data } = await api.post<CustomerAuthResponse>("/api/customer-auth/register", payload)
  return data
}