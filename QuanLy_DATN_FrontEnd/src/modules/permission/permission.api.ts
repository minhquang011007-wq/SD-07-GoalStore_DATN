import api from "../../shared/lib/api"
import type { PermissionResponse } from "./types"

export type UpdateRolePermissionsBody = {
  role: string
  permissionCodes: string[]
}

export const permissionApi = {
  getAll: async () => {
    const res = await api.get<PermissionResponse[]>("/api/permissions")
    return res.data
  },

  getByRole: async (role: string) => {
    const res = await api.get<PermissionResponse[]>(`/api/permissions/role/${role}`)
    return res.data
  },

  updateRolePermissions: async (body: UpdateRolePermissionsBody) => {
    const res = await api.put<Record<string, any>>("/api/permissions/role", body)
    return res.data
  },

  grant: async (role: string, code: string) => {
    const res = await api.post<Record<string, any>>(`/api/permissions/role/${role}/grant/${code}`)
    return res.data
  },

  revoke: async (role: string, code: string) => {
    const res = await api.delete<Record<string, any>>(`/api/permissions/role/${role}/revoke/${code}`)
    return res.data
  },
}