import { apiRequest } from '@/shared/lib/api'

export type StaffLoginResponse = {
  token: string
  email: string
  role: 'ADMIN' | 'SALES' | 'INVENTORY'
}

export type CustomerLoginResponse = {
  token: string
  customerId: number
  email: string
  ten: string
  role: 'CUSTOMER'
}

type MockCustomer = {
  customerId: number
  ten: string
  email: string
  sdt: string
  password: string
  ngaySinh?: string
}

type StaffApiResponse = {
  token: string
  username?: string
  email?: string
  role: 'ADMIN' | 'SALES' | 'INVENTORY'
}

const CUSTOMER_STORAGE_KEY = 'goalstore_mock_customers'
const STAFF_ACCOUNTS: Array<StaffLoginResponse & { password: string }> = [
  { token: 'mock-admin-token', email: 'admin@goalstore.vn', role: 'ADMIN', password: '123456' },
]

function delay<T>(value: T, ms = 80) {
  return new Promise<T>((resolve) => setTimeout(() => resolve(value), ms))
}

function createToken(prefix: string) {
  return `${prefix}-${Math.random().toString(36).slice(2)}-${Date.now()}`
}

function seedCustomers() {
  return [
    {
      customerId: 1,
      ten: 'Khách hàng demo',
      email: 'demo@goalstore.vn',
      sdt: '0912345678',
      password: '123456',
      ngaySinh: '2000-01-01',
    },
  ] as MockCustomer[]
}

function readCustomers() {
  if (typeof window === 'undefined') return seedCustomers()
  try {
    const raw = window.localStorage.getItem(CUSTOMER_STORAGE_KEY)
    if (!raw) {
      const seeded = seedCustomers()
      window.localStorage.setItem(CUSTOMER_STORAGE_KEY, JSON.stringify(seeded))
      return seeded
    }
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? (parsed as MockCustomer[]) : seedCustomers()
  } catch {
    return seedCustomers()
  }
}

function writeCustomers(customers: MockCustomer[]) {
  if (typeof window === 'undefined') return
  window.localStorage.setItem(CUSTOMER_STORAGE_KEY, JSON.stringify(customers))
}

function normalizeEmail(email: string) {
  return email.trim().toLowerCase()
}

function isNetworkError(error: unknown) {
  const message = error instanceof Error ? error.message.toLowerCase() : ''
  return message.includes('failed to fetch') || message.includes('networkerror') || message.includes('load failed')
}

async function loginStaffMock(payload: { email: string; password: string }) {
  const staff = STAFF_ACCOUNTS.find(
    (item) => item.email.toLowerCase() === payload.email.trim().toLowerCase() && item.password === payload.password,
  )

  if (!staff) {
    throw new Error('Tài khoản nhân viên không đúng')
  }

  return delay({ token: createToken('staff'), email: staff.email, role: staff.role })
}

async function loginCustomerMock(payload: { email: string; password: string }) {
  const customer = readCustomers().find(
    (item) => item.email.toLowerCase() === payload.email.trim().toLowerCase() && item.password === payload.password,
  )

  if (!customer) {
    throw new Error('Sai email hoặc mật khẩu')
  }

  return delay({
    token: createToken('customer'),
    customerId: customer.customerId,
    email: customer.email,
    ten: customer.ten,
    role: 'CUSTOMER' as const,
  })
}

async function registerCustomerMock(payload: {
  ten: string
  email: string
  sdt: string
  password: string
  ngaySinh?: string
}) {
  const customers = readCustomers()
  const normalizedEmail = normalizeEmail(payload.email)

  if (customers.some((item) => item.email.toLowerCase() === normalizedEmail)) {
    throw new Error('Email đã tồn tại trong hệ thống')
  }

  const nextId = customers.length ? Math.max(...customers.map((item) => item.customerId)) + 1 : 1
  const customer: MockCustomer = {
    customerId: nextId,
    ten: payload.ten.trim(),
    email: normalizedEmail,
    sdt: payload.sdt.trim(),
    password: payload.password,
    ngaySinh: payload.ngaySinh,
  }

  customers.push(customer)
  writeCustomers(customers)

  return delay({
    token: createToken('customer'),
    customerId: customer.customerId,
    email: customer.email,
    ten: customer.ten,
    role: 'CUSTOMER' as const,
  })
}

export async function loginStaff(payload: { email: string; password: string }) {
  try {
    const response = await apiRequest<StaffApiResponse>('/api/auth/login', {
      method: 'POST',
      body: {
        email: normalizeEmail(payload.email),
        password: payload.password,
      },
      skipAuth: true,
    })

    return {
      token: response.token,
      email: response.email || response.username || normalizeEmail(payload.email),
      role: response.role,
    } as StaffLoginResponse
  } catch (error) {
    if (!isNetworkError(error)) {
      throw error
    }
  }

  return loginStaffMock(payload)
}

export async function loginCustomer(payload: { email: string; password: string }) {
  try {
    return await apiRequest<CustomerLoginResponse>('/api/customer-auth/login', {
      method: 'POST',
      body: {
        email: normalizeEmail(payload.email),
        password: payload.password,
      },
      skipAuth: true,
    })
  } catch (error) {
    if (!isNetworkError(error)) {
      throw error
    }
  }

  return loginCustomerMock(payload)
}

export async function registerCustomer(payload: {
  ten: string
  email: string
  sdt: string
  password: string
  ngaySinh?: string
}) {
  try {
    return await apiRequest<CustomerLoginResponse>('/api/customer-auth/register', {
      method: 'POST',
      body: {
        ten: payload.ten.trim(),
        email: normalizeEmail(payload.email),
        sdt: payload.sdt.trim(),
        password: payload.password,
        ngaySinh: payload.ngaySinh,
      },
      skipAuth: true,
    })
  } catch (error) {
    if (!isNetworkError(error)) {
      throw error
    }
  }

  return registerCustomerMock(payload)
}
