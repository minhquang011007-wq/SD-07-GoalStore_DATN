import api from '@/service/axios'
import type {
  AddPointRequest,
  BirthdayLog,
  DeductPointRequest,
  LoyaltyCustomer,
  LoyaltyHistory,
  RewardRule,
  VipHistory,
  VipProgram
} from '../types/loyalty'

export const addPoint = async (payload: AddPointRequest) => {
  const res = await api.post('/api/loyalty/add', payload)
  return res.data
}

export const deductPoint = async (payload: DeductPointRequest) => {
  const res = await api.post('/api/loyalty/deduct', payload)
  return res.data
}

export const getLoyaltyCustomers = async () => {
  const res = await api.get<LoyaltyCustomer[]>('/api/loyalty/customers')
  return res.data
}

export const getLoyaltyHistory = async (customerId: number) => {
  const res = await api.get<LoyaltyHistory[]>(`/api/loyalty/history/${customerId}`)
  return res.data
}

export const getVipPrograms = async () => {
  const res = await api.get<VipProgram[]>('/api/loyalty/vip-programs')
  return res.data
}

export const getVipHistory = async (customerId: number) => {
  const res = await api.get<VipHistory[]>(`/api/loyalty/vip-history/${customerId}`)
  return res.data
}

export const getRewardRules = async () => {
  const res = await api.get<RewardRule[]>('/api/loyalty/reward-rules')
  return res.data
}

export const getBirthdayLogs = async (customerId: number) => {
  const res = await api.get<BirthdayLog[]>(`/api/loyalty/birthday-logs/${customerId}`)
  return res.data
}