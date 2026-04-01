export interface CustomerTransactionItem {
  id?: number;
  variant_id?: number;
  quantity: number;
  unit_price: number;
}

export interface CustomerTransaction {
  id: number;
  order_date?: string | null;
  created_at?: string | null;
  status: string;
  total: number;
  payment_method?: string;
  channel?: string;
  points_earned?: number;
  items: CustomerTransactionItem[];
}