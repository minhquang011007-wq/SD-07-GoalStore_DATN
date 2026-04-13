import api from '@/shared/lib/api'
import type { AdminDashboardResponse, MonthlyMetric, StatusMetric, TopCustomerMetric } from '../types/dashboard'

function toNumber(value: unknown): number {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

function normalizeMonthly(items: any[] = []): MonthlyMetric[] {
  return items.map((item) => ({
    month: toNumber(item?.month),
    label: String(item?.label || ''),
    value: toNumber(item?.value),
  }))
}

function normalizeStatus(items: any[] = []): StatusMetric[] {
  return items.map((item) => ({
    status: String(item?.status || ''),
    label: String(item?.label || ''),
    count: toNumber(item?.count),
  }))
}

function normalizeTopCustomers(items: any[] = []): TopCustomerMetric[] {
  return items.map((item) => ({
    customerId: toNumber(item?.customerId),
    customerName: String(item?.customerName || ''),
    totalOrders: toNumber(item?.totalOrders),
    totalSpent: toNumber(item?.totalSpent),
    loyaltyPoints: toNumber(item?.loyaltyPoints),
    customerType: String(item?.customerType || 'THUONG'),
  }))
}

export async function getAdminDashboard(year: number): Promise<AdminDashboardResponse> {
  const { data } = await api.get('/api/dashboard/admin', {
    params: { year },
  })

  return {
    year: toNumber(data?.year),
    summary: {
      totalOrders: toNumber(data?.summary?.totalOrders),
      completedOrders: toNumber(data?.summary?.completedOrders),
      cancelledOrders: toNumber(data?.summary?.cancelledOrders),
      returnedOrders: toNumber(data?.summary?.returnedOrders),
      totalRevenue: toNumber(data?.summary?.totalRevenue),
      averageOrderValue: toNumber(data?.summary?.averageOrderValue),
    },
    customers: {
      totalCustomers: toNumber(data?.customers?.totalCustomers),
      activeCustomers: toNumber(data?.customers?.activeCustomers),
      vipCustomers: toNumber(data?.customers?.vipCustomers),
      newCustomersThisYear: toNumber(data?.customers?.newCustomersThisYear),
    },
    vouchers: {
      totalVouchers: toNumber(data?.vouchers?.totalVouchers),
      activeVouchers: toNumber(data?.vouchers?.activeVouchers),
      claimedVouchers: toNumber(data?.vouchers?.claimedVouchers),
      usedVouchers: toNumber(data?.vouchers?.usedVouchers),
    },
    revenueByMonth: normalizeMonthly(data?.revenueByMonth),
    ordersByMonth: normalizeMonthly(data?.ordersByMonth),
    orderStatusBreakdown: normalizeStatus(data?.orderStatusBreakdown),
    topCustomers: normalizeTopCustomers(data?.topCustomers),
  }
}