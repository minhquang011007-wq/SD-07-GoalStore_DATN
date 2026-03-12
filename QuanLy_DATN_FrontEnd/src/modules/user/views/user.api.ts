import api from "@/shared/lib/api"
import type { PageResponse, UserResponse } from "./types"

export type GetUsersParams = {
  q?: string
  role?: string
  trangThai?: string
  page?: number
  size?: number
}

export type CreateUserBody = {
  username: string
  email?: string | null
  password: string
  role: string
  trangThai: string
}

export type UpdateUserBody = {
  email?: string | null
  role?: string | null
  trangThai?: string | null
}

export type ChangeStatusBody = { trangThai: string }
export type ResetPasswordBody = { newPassword: string }

export const userApi = {
  getUsers: async (params: GetUsersParams) => {
    const res = await api.get<PageResponse<UserResponse>>("/api/users", { params })
    return res.data
  },

  getUserById: async (id: number) => {
    const res = await api.get<UserResponse>(`/api/users/${id}`)
    return res.data
  },

  createUser: async (body: CreateUserBody) => {
    const res = await api.post<UserResponse>("/api/users", body)
    return res.data
  },

  updateUser: async (id: number, body: UpdateUserBody) => {
    const res = await api.put<UserResponse>(`/api/users/${id}`, body)
    return res.data
  },

  changeStatus: async (id: number, body: ChangeStatusBody) => {
    const res = await api.patch<UserResponse>(`/api/users/${id}/status`, body)
    return res.data
  },

  resetPassword: async (id: number, body: ResetPasswordBody) => {
    const res = await api.post<string>(`/api/users/${id}/reset-password`, body)
    return res.data
  },

  deleteUser: async (id: number) => {
    const res = await api.delete<string>(`/api/users/${id}`)
    return res.data
  },
}