export type StoreProduct = {
  id: number
  name: string
  brand: string
  category: string
  price: number
  salePrice?: number | null
  image: string
  isNew?: boolean
}
