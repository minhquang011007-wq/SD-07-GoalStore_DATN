export type MonthlyMetric = {
  month: number
  label: string
  value: number
}

export type StatusMetric = {
  status: string
  label: string
  count: number
}

export type TopCustomerMetric = {
  customerId: number
  customerName: string
  totalOrders: number
  totalSpent: number
  loyaltyPoints: number
  customerType: string
}

export type AdminDashboardResponse = {
  year: number
  summary: {
    totalOrders: number
    completedOrders: number
    cancelledOrders: number
    returnedOrders: number
    totalRevenue: number
    averageOrderValue: number
  }
  customers: {
    totalCustomers: number
    activeCustomers: number
    vipCustomers: number
    newCustomersThisYear: number
  }
  vouchers: {
    totalVouchers: number
    activeVouchers: number
    claimedVouchers: number
    usedVouchers: number
  }
  revenueByMonth: MonthlyMetric[]
  ordersByMonth: MonthlyMetric[]
  orderStatusBreakdown: StatusMetric[]
  topCustomers: TopCustomerMetric[]
}