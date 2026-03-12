export interface AddPointRequest {
  customerId: number
  points: number
  note: string
}

export interface DeductPointRequest {
  customerId: number
  points: number
  note: string
}

export interface LoyaltyCustomer {
  customerId: number
  ten: string
  email: string
  sdt: string
  diemTichLuy: number
  loaiKhach: string
}

export interface LoyaltyHistory {
  id: number
  customerId: number
  points: number
  type: string
  note: string
  createdAt: string
}

export interface VipProgram {
  id: number
  levelName: string
  minPoints: number
  minSpending: number
  benefit: string
  isActive: boolean
}

export interface VipHistory {
  id: number
  customerId: number
  oldLevel: string
  newLevel: string
  reason: string
  changedAt: string
}

export interface RewardRule {
  id: number
  rewardName: string
  requiredPoints: number
  discountValue: number
  description: string
  isActive: boolean
}

export interface BirthdayLog {
  id: number
  customerId: number
  ten: string
  channel: string
  note: string
  sentDate: string
}