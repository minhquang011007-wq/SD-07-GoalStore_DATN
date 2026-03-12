// customerApi.ts
import api from '@/service/axios'
import type {
  Customer,
  CustomerOrderHistory,
  CustomerSpending,
  InactiveCustomer
} from '../types/customer'

export const getCustomers = async () => {
  const res = await api.get<Customer[]>('/api/customers')
  return res.data
}

export const getCustomerById = async (id: number) => {
  const res = await api.get<Customer>(`/api/customers/${id}`)
  return res.data
}

export const createCustomer = async (payload: Customer) => {
  const res = await api.post<Customer>('/api/customers', payload)
  return res.data
}

export const updateCustomer = async (id: number, payload: Customer) => {
  const res = await api.put<Customer>(`/api/customers/${id}`, payload)
  return res.data
}

export const deleteCustomer = async (id: number) => {
  const res = await api.delete<string>(`/api/customers/${id}`)
  return res.data
}

export const searchCustomerByName = async (name: string) => {
  const res = await api.get<Customer[]>('/api/customers/search', { params: { name } })
  return res.data
}

export const filterCustomerByLoaiKhach = async (loaiKhach: string) => {
  const res = await api.get<Customer[]>('/api/customers/filter', { params: { loaiKhach } })
  return res.data
}

export const searchCustomerByEmail = async (email: string) => {
  const res = await api.get<Customer[]>('/api/customers/email', { params: { email } })
  return res.data
}

export const searchCustomerByPhone = async (sdt: string) => {
  const res = await api.get<Customer[]>('/api/customers/phone', { params: { sdt } })
  return res.data
}

export const getCustomerOrders = async (customerId: number) => {
  const res = await api.get<CustomerOrderHistory[]>(`/api/customers/${customerId}/orders`)
  return res.data
}

export const getTopSpendingCustomers = async () => {
  const res = await api.get<CustomerSpending[]>('/api/customers/top-spending')
  return res.data
}

export const getInactiveCustomers = async (days: number) => {
  const res = await api.get<InactiveCustomer[]>('/api/customers/inactive', { params: { days } })
  return res.data
}