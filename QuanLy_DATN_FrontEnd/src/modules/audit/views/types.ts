export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface AuditLogResponse {
  id: number
  userId: number | null
  username: string | null
  action: string
  entity: string
  entityId: number | null
  createdAt: string
  detail: string | null
}

export interface LoginAttemptResponse {
  id: number
  userId: number | null
  username: string
  success: boolean
  ipAddress: string | null
  userAgent: string | null
  createdAt: string
}

export interface SecurityAlertResponse {
  id: number
  userId: number | null
  username: string | null
  alertType: string
  severity: string
  message: string
  ipAddress: string | null
  createdAt: string
  resolved: boolean
}

export interface AuditReportItemResponse {
  key: string
  total: number
}

export interface AuditDashboardResponse {
  totalLogs: number
  totalLoginSuccess: number
  totalLoginFailed: number
  totalAlerts: number
  totalCriticalAlerts: number
  sensitiveActions: number
  logsByModule: Record<string, number>
  logsByAction: Record<string, number>
}