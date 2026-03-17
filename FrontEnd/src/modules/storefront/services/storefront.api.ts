import api from "@/shared/lib/api"
import type { StoreProduct } from "@/modules/storefront/types"

const fallbackProducts: StoreProduct[] = [
  { id: 1, name: "Piqué Biker Jacket", brand: "GoalStore", category: "Outerwear", price: 1670000, salePrice: 1490000, image: "/storefront/product/product-1.jpg", isNew: true },
  { id: 2, name: "Diagonal Textured Cap", brand: "GoalStore", category: "Accessories", price: 490000, image: "/storefront/product/product-2.jpg" },
  { id: 3, name: "Lether Backpack", brand: "GoalStore", category: "Bags", price: 990000, salePrice: 850000, image: "/storefront/product/product-3.jpg" },
  { id: 4, name: "Ankle Boots", brand: "GoalStore", category: "Shoes", price: 1290000, image: "/storefront/product/product-4.jpg" },
  { id: 5, name: "Basic Flowing Scarf", brand: "GoalStore", category: "Accessories", price: 320000, image: "/storefront/product/product-5.jpg" },
  { id: 6, name: "Cotton T-Shirt", brand: "GoalStore", category: "Tops", price: 380000, image: "/storefront/product/product-6.jpg", isNew: true },
  { id: 7, name: "Slim Fit Trousers", brand: "GoalStore", category: "Bottoms", price: 620000, image: "/storefront/product/product-7.jpg" },
  { id: 8, name: "Urban Sneaker", brand: "GoalStore", category: "Shoes", price: 1150000, salePrice: 980000, image: "/storefront/product/product-8.jpg" },
]

function mapBackendProducts(items: any[]): StoreProduct[] {
  return items.map((item, index) => ({
    id: item.id ?? index + 1,
    name: item.name ?? item.productName ?? "Product",
    brand: item.brand ?? "GoalStore",
    category: item.categoryName ?? item.category ?? "Fashion",
    price: Number(item.salePrice ?? item.price ?? 0),
    salePrice: item.originalPrice ? Number(item.price) : undefined,
    image: item.thumbnailUrl || item.imageUrl || item.avatarUrl || fallbackProducts[index % fallbackProducts.length].image,
    isNew: Boolean(item.isNew ?? false),
  }))
}

export async function getStoreProducts(): Promise<StoreProduct[]> {
  try {
    const res = await api.get("/api/public/products", {
      params: { page: 0, size: 8, sort: "newest" },
    })

    const raw = res.data?.content || res.data?.data?.content || res.data?.data || res.data || []
    if (Array.isArray(raw) && raw.length > 0) {
      return mapBackendProducts(raw)
    }
    return fallbackProducts
  } catch (error) {
    console.warn("Fallback storefront products because public API is not ready:", error)
    return fallbackProducts
  }
}
