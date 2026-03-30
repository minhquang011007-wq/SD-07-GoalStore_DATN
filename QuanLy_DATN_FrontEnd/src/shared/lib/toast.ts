import { reactive } from "vue"

type ToastType = "success" | "error" | "info"

export const toastStore = reactive({
  open: false,
  type: "info" as ToastType,
  message: "",
})

export function showToast(message: string, type: ToastType = "info", ms = 2500) {
  toastStore.open = true
  toastStore.type = type
  toastStore.message = message
  window.setTimeout(() => (toastStore.open = false), ms)
}