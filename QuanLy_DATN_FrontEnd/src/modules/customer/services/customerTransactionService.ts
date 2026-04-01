import api from "@/shared/lib/api";
import type { CustomerTransaction } from "../types/customerTransaction";

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function normalizeTransaction(raw: any): CustomerTransaction {
  return {
    id: toNumber(raw?.orderId ?? raw?.id, 0),
    order_date: raw?.orderDate ?? raw?.order_date ?? null,
    created_at: raw?.createdAt ?? raw?.created_at ?? raw?.orderDate ?? null,
    status: raw?.status ?? "MOI",
    total: toNumber(raw?.total, 0),
    payment_method: raw?.paymentMethod ?? raw?.payment_method ?? "",
    channel: raw?.channel ?? "",
    points_earned: toNumber(raw?.pointsEarned ?? raw?.points_earned, 0),
    items: Array.isArray(raw?.items)
      ? raw.items.map((item: any) => ({
          id: toNumber(item?.id, 0),
          variant_id: toNumber(item?.variantId ?? item?.variant_id, 0),
          quantity: toNumber(item?.quantity, 0),
          unit_price: toNumber(item?.unitPrice ?? item?.unit_price, 0),
        }))
      : [],
  };
}

function unwrapList(payload: any): any[] {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.data)) return payload.data;
  return [];
}

export async function getTransactionsByCustomer(customerId: number): Promise<CustomerTransaction[]> {
  const res = await api.get(`/api/customers/${customerId}/orders`);
  return unwrapList(res.data).map(normalizeTransaction);
}