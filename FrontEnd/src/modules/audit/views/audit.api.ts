import api from "@/shared/lib/api"
import type {
  AuditDashboardResponse,
  AuditLogResponse,
  AuditReportItemResponse,
  LoginAttemptResponse,
  PageResponse,
  SecurityAlertResponse,
} from "./types"

export const auditApi = {
  getAuditLogs: async (params: any) => {
    const res = await api.get<PageResponse<AuditLogResponse>>("/api/audit", { params })
    return res.data
  },

  getLoginHistory: async (params: any) => {
    const res = await api.get<PageResponse<LoginAttemptResponse>>("/api/audit/login-history", { params })
    return res.data
  },

  getDashboard: async (params: any) => {
    const res = await api.get<AuditDashboardResponse>("/api/audit/dashboard", { params })
    return res.data
  },

  reportByModule: async (params: any) => {
    const res = await api.get<AuditReportItemResponse[]>("/api/audit/report/module", { params })
    return res.data
  },

  reportByUser: async (params: any) => {
    const res = await api.get<AuditReportItemResponse[]>("/api/audit/report/user", { params })
    return res.data
  },

  exportCsv: async (params: any) => {
    const res = await api.get("/api/audit/export/csv", { params, responseType: "blob" })
    return res.data as Blob
  },

  getAlerts: async (params: any) => {
    const res = await api.get<PageResponse<SecurityAlertResponse>>("/api/audit/alerts", { params })
    return res.data
  },

  resolveAlert: async (id: number) => {
    const res = await api.patch<SecurityAlertResponse>(`/api/audit/alerts/${id}/resolve`)
    return res.data
  },
}