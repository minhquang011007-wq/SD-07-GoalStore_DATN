export type Role = "ADMIN" | "SALES" | "INVENTORY"

export interface PermissionResponse {
  id: number
  code: string
  name: string
  module: string
}