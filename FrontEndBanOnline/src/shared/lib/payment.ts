export type QrOrderLike = {
  id?: number | string | null
  code?: string | null
  total?: number | string | null
}

const QR_BANK_ID = String(import.meta.env.VITE_QR_BANK_ID || 'mbbank').trim()
const QR_ACCOUNT_NO = String(import.meta.env.VITE_QR_ACCOUNT_NO || '0966026145').trim()
const QR_ACCOUNT_NAME = String(import.meta.env.VITE_QR_ACCOUNT_NAME || 'Vũ Chí Tuấn Anh').trim()
const QR_TEMPLATE = String(import.meta.env.VITE_QR_TEMPLATE || 'compact2').trim()

function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function normalizeTransferContent(value: string) {
  return value
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .replace(/[^a-zA-Z0-9 ]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
    .slice(0, 50)
}

export function isQrPaymentMethod(value?: string | null) {
  const normalized = String(value || '').trim().toUpperCase()
  return normalized === 'QR'
}

export function getQrBankInfo() {
  return {
    bankId: QR_BANK_ID,
    accountNo: QR_ACCOUNT_NO,
    accountName: QR_ACCOUNT_NAME,
    template: QR_TEMPLATE,
  }
}

export function buildQrTransferContent(order?: QrOrderLike | null) {
  const base = String(order?.code || (order?.id ? `DH${order.id}` : 'GOALSTORE'))
  return normalizeTransferContent(base || 'GOALSTORE')
}

export function buildQrImageUrl(order?: QrOrderLike | null) {
  const amount = Math.max(0, Math.round(toNumber(order?.total)))
  const addInfo = buildQrTransferContent(order)

  const base = `https://img.vietqr.io/image/${encodeURIComponent(QR_BANK_ID)}-${encodeURIComponent(QR_ACCOUNT_NO)}-${encodeURIComponent(QR_TEMPLATE)}.png`
  const query = new URLSearchParams({
    amount: String(amount),
    addInfo,
    accountName: QR_ACCOUNT_NAME,
  })

  return `${base}?${query.toString()}`
}
