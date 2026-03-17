export type Role = "ADMIN" | "SALES" | "INVENTORY"
export type UserStatus = "ACTIVE" | "INACTIVE" | "LOCKED"

export interface UserResponse {
  id: number
  username: string
  email: string | null
  role: Role
  trangThai: UserStatus
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}