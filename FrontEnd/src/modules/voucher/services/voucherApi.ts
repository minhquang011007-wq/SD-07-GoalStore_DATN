import api from '@/shared/lib/api'
import type { VoucherItem, VoucherPayload } from '../types/voucher'

export const getVouchers = async () => {
  const res = await api.get<VoucherItem[]>('/api/vouchers/admin')
  return res.data
}

export const createVoucher = async (payload: VoucherPayload) => {
  const res = await api.post<VoucherItem>('/api/vouchers/admin', payload)
  return res.data
}

export const updateVoucher = async (id: number, payload: VoucherPayload) => {
  const res = await api.put<VoucherItem>(`/api/vouchers/admin/${id}`, payload)
  return res.data
}

export const updateVoucherStatus = async (id: number, isActive: boolean) => {
  const res = await api.patch<VoucherItem>(`/api/vouchers/admin/${id}/status`, { isActive })
  return res.data
}

export const deleteVoucher = async (id: number) => {
  await api.delete(`/api/vouchers/admin/${id}`)
}
