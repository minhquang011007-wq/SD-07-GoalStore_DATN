export interface CustomerFilter {
  keyword: string;
  loai_khach: "" | "VIP" | "THUONG";
  inactiveDays: number;
  minPoints?: number | null;
  maxPoints?: number | null;
  minSpending?: number | null;
  maxSpending?: number | null;
  sortBy: "newest" | "name" | "points-desc" | "spending-desc" | "inactive-desc";
}