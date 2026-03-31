import api from "@/shared/lib/api"

export type CustomerLoginPayload = {
  email: string
  password: string
}

export type CustomerLoginResponse = {
  token: string
  customerId: number
  email: string
  ten: string
  role: "CUSTOMER"
}

export async function loginCustomer(payload: CustomerLoginPayload) {
  const { data } = await api.post<CustomerLoginResponse>("/api/customer-auth/login", payload)
  return data
}
