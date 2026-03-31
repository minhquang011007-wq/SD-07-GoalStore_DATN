import type {
  ProductAttribute,
  ProductCategory,
  ProductImage,
  ProductVariant,
  PublicProductDetail,
  PublicProductSummary,
} from '@/shared/lib/shop.types'

const categoryGiayCoNhanTao: ProductCategory = { id: 1, name: 'Giày sân cỏ nhân tạo', productCount: 4 }
const categoryFutsal: ProductCategory = { id: 2, name: 'Giày futsal', productCount: 4 }
const categoryPhuKien: ProductCategory = { id: 3, name: 'Phụ kiện bóng đá', productCount: 4 }
const categoryTapLuyen: ProductCategory = { id: 4, name: 'Phụ kiện luyện tập', productCount: 2 }

function images(...urls: string[]): ProductImage[] {
  return urls.map((imageUrl, index) => ({
    id: index + 1,
    imageUrl,
    avatar: index === 0,
    sortOrder: index,
  }))
}

function attrs(...items: Array<[string, string]>): ProductAttribute[] {
  return items.map(([name, value], index) => ({ id: index + 1, name, value }))
}

function variant(
  id: number,
  size: string,
  color: string,
  price: number,
  salePrice: number | null,
  stockQuantity: number,
  sku: string,
): ProductVariant {
  return {
    id,
    size,
    color,
    price,
    salePrice,
    stockQuantity,
    stockStatus: stockQuantity > 0 ? 'IN_STOCK' : 'OUT_OF_STOCK',
    sku,
  }
}

export const mockProducts: PublicProductDetail[] = [
  {
    id: 101,
    name: 'Nike Mercurial Vapor 15 Academy TF',
    baseSku: 'NIKE-TF-001',
    brand: 'Nike',
    season: '2026',
    productType: 'Giày',
    targetGender: 'Unisex',
    material: 'Synthetic',
    description:
      'Mẫu giày thiên về tốc độ, ôm chân tốt, phù hợp sân cỏ nhân tạo 5-7 người. Đế bám ổn và cảm giác bóng gọn chân.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-1.jpg',
    categories: [categoryGiayCoNhanTao],
    images: images(
      '/legacy/img/product/product-1.jpg',
      '/legacy/img/shop-details/product-big.png',
      '/legacy/img/shop-details/product-big-2.png',
    ),
    variants: [
      variant(1001, '39', 'Đỏ', 1590000, 1390000, 6, 'NK-MV15-TF-39-RED'),
      variant(1002, '40', 'Đỏ', 1590000, 1390000, 8, 'NK-MV15-TF-40-RED'),
      variant(1003, '41', 'Đỏ', 1590000, 1390000, 5, 'NK-MV15-TF-41-RED'),
      variant(1004, '42', 'Đen', 1590000, null, 4, 'NK-MV15-TF-42-BLK'),
    ],
    attributes: attrs(['Form giày', 'Ôm chân, thiên tốc độ'], ['Mặt sân', 'Cỏ nhân tạo'], ['Đệm', 'Êm vừa']),
  },
  {
    id: 102,
    name: 'Adidas Predator Accuracy.3 TF',
    baseSku: 'ADI-TF-002',
    brand: 'Adidas',
    season: '2026',
    productType: 'Giày',
    targetGender: 'Unisex',
    material: 'Textile',
    description:
      'Thiết kế cho cảm giác chạm bóng chắc chắn, phù hợp người chơi thích chuyền bóng và sút lực trên sân cỏ nhân tạo.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-2.jpg',
    categories: [categoryGiayCoNhanTao],
    images: images('/legacy/img/product/product-2.jpg', '/legacy/img/shop-details/product-big-3.png'),
    variants: [
      variant(1005, '39', 'Xanh', 1690000, 1490000, 7, 'ADI-PRED-TF-39-BLU'),
      variant(1006, '40', 'Xanh', 1690000, 1490000, 9, 'ADI-PRED-TF-40-BLU'),
      variant(1007, '41', 'Trắng', 1690000, null, 3, 'ADI-PRED-TF-41-WHT'),
      variant(1008, '42', 'Trắng', 1690000, null, 2, 'ADI-PRED-TF-42-WHT'),
    ],
    attributes: attrs(['Form giày', 'Cân bằng'], ['Mặt sân', 'Cỏ nhân tạo'], ['Điểm nổi bật', 'Hỗ trợ kiểm soát bóng']),
  },
  {
    id: 103,
    name: 'Mizuno Monarcida Neo Sala Club IN',
    baseSku: 'MIZ-IN-003',
    brand: 'Mizuno',
    season: '2025',
    productType: 'Giày',
    targetGender: 'Unisex',
    material: 'Da tổng hợp',
    description: 'Mẫu futsal dễ đi, phom vừa chân, thích hợp tập luyện và thi đấu phong trào trong nhà.',
    releaseYear: 2025,
    thumbnailUrl: '/legacy/img/product/product-3.jpg',
    categories: [categoryFutsal],
    images: images('/legacy/img/product/product-3.jpg', '/legacy/img/shop-details/product-big-4.png'),
    variants: [
      variant(1009, '39', 'Trắng', 1290000, 1190000, 10, 'MIZ-SALA-39-WHT'),
      variant(1010, '40', 'Trắng', 1290000, 1190000, 12, 'MIZ-SALA-40-WHT'),
      variant(1011, '41', 'Đen', 1290000, null, 4, 'MIZ-SALA-41-BLK'),
      variant(1012, '42', 'Đen', 1290000, null, 1, 'MIZ-SALA-42-BLK'),
    ],
    attributes: attrs(['Mặt sân', 'Trong nhà'], ['Độ êm', 'Tốt'], ['Phong cách', 'Dễ mang hằng ngày']),
  },
  {
    id: 104,
    name: 'Puma Future Play IT',
    baseSku: 'PUMA-IN-004',
    brand: 'Puma',
    season: '2025',
    productType: 'Giày',
    targetGender: 'Unisex',
    material: 'Synthetic',
    description: 'Giày futsal đế bám tốt, upper mềm, phù hợp người chơi thích rê dắt và đổi hướng nhanh.',
    releaseYear: 2025,
    thumbnailUrl: '/legacy/img/product/product-4.jpg',
    categories: [categoryFutsal],
    images: images('/legacy/img/product/product-4.jpg', '/legacy/img/product/product-8.jpg'),
    variants: [
      variant(1013, '39', 'Vàng', 1390000, 1250000, 6, 'PUMA-FUT-39-YEL'),
      variant(1014, '40', 'Vàng', 1390000, 1250000, 8, 'PUMA-FUT-40-YEL'),
      variant(1015, '41', 'Đen', 1390000, null, 5, 'PUMA-FUT-41-BLK'),
      variant(1016, '42', 'Đen', 1390000, null, 0, 'PUMA-FUT-42-BLK'),
    ],
    attributes: attrs(['Mặt sân', 'Trong nhà'], ['Form giày', 'Hơi bè'], ['Điểm nổi bật', 'Đổi hướng linh hoạt']),
  },
  {
    id: 105,
    name: 'Kamito TA11 Pro TF',
    baseSku: 'KMT-TF-005',
    brand: 'Kamito',
    season: '2026',
    productType: 'Giày',
    targetGender: 'Nam',
    material: 'Da tổng hợp',
    description: 'Lựa chọn tầm giá tốt cho sân phủi, đế bám ổn, form dễ mang, phù hợp người mới chơi lẫn đá phong trào.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-5.jpg',
    categories: [categoryGiayCoNhanTao],
    images: images('/legacy/img/product/product-5.jpg', '/legacy/img/product/product-9.jpg'),
    variants: [
      variant(1017, '39', 'Xanh', 890000, 790000, 15, 'KMT-TA11-39-BLU'),
      variant(1018, '40', 'Xanh', 890000, 790000, 11, 'KMT-TA11-40-BLU'),
      variant(1019, '41', 'Đỏ', 890000, null, 6, 'KMT-TA11-41-RED'),
      variant(1020, '42', 'Đỏ', 890000, null, 5, 'KMT-TA11-42-RED'),
    ],
    attributes: attrs(['Mặt sân', 'Cỏ nhân tạo'], ['Tệp khách hàng', 'Phong trào'], ['Điểm mạnh', 'Giá tốt, dễ đi']),
  },
  {
    id: 106,
    name: 'Zocker Inspire Futsal',
    baseSku: 'ZOC-IN-006',
    brand: 'Zocker',
    season: '2026',
    productType: 'Giày',
    targetGender: 'Nam',
    material: 'PU',
    description: 'Giày futsal giá dễ tiếp cận, phù hợp tập luyện hằng tuần, upper mềm và ôm vừa chân.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-6.jpg',
    categories: [categoryFutsal],
    images: images('/legacy/img/product/product-6.jpg', '/legacy/img/product/product-10.jpg'),
    variants: [
      variant(1021, '39', 'Cam', 690000, 590000, 13, 'ZOC-INS-39-ORG'),
      variant(1022, '40', 'Cam', 690000, 590000, 9, 'ZOC-INS-40-ORG'),
      variant(1023, '41', 'Xám', 690000, null, 8, 'ZOC-INS-41-GRY'),
      variant(1024, '42', 'Xám', 690000, null, 2, 'ZOC-INS-42-GRY'),
    ],
    attributes: attrs(['Mặt sân', 'Trong nhà'], ['Mức giá', 'Dễ tiếp cận'], ['Điểm mạnh', 'Phù hợp người mới']),
  },
  {
    id: 107,
    name: 'Găng tay thủ môn T90 Grip',
    baseSku: 'ACC-GK-007',
    brand: 'T90',
    season: '2026',
    productType: 'Phụ kiện',
    targetGender: 'Unisex',
    material: 'Latex',
    description: 'Găng tay thủ môn bám dính ổn, cổ tay chắc, thích hợp thi đấu và tập luyện phong trào.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-7.jpg',
    categories: [categoryPhuKien],
    images: images('/legacy/img/product/product-7.jpg', '/legacy/img/shopping-cart/cart-1.jpg'),
    variants: [
      variant(1025, '8', 'Đen', 390000, 329000, 18, 'T90-GK-08-BLK'),
      variant(1026, '9', 'Đen', 390000, 329000, 12, 'T90-GK-09-BLK'),
      variant(1027, '10', 'Đỏ', 390000, 329000, 7, 'T90-GK-10-RED'),
    ],
    attributes: attrs(['Loại', 'Găng tay thủ môn'], ['Độ bám', 'Tốt trong điều kiện khô'], ['Đối tượng', 'Phong trào']),
  },
  {
    id: 108,
    name: 'Bảo vệ ống đồng Flex Shield',
    baseSku: 'ACC-SHIN-008',
    brand: 'Flex',
    season: '2025',
    productType: 'Phụ kiện',
    targetGender: 'Unisex',
    material: 'PP + EVA',
    description: 'Miếng bảo vệ ống đồng gọn nhẹ, ôm chân vừa phải, phù hợp thi đấu sân phủi.',
    releaseYear: 2025,
    thumbnailUrl: '/legacy/img/product/product-8.jpg',
    categories: [categoryPhuKien],
    images: images('/legacy/img/product/product-8.jpg', '/legacy/img/shopping-cart/cart-2.jpg'),
    variants: [
      variant(1028, 'S', 'Trắng', 149000, null, 20, 'FLX-SHIN-S-WHT'),
      variant(1029, 'M', 'Trắng', 149000, null, 25, 'FLX-SHIN-M-WHT'),
      variant(1030, 'L', 'Đen', 149000, null, 16, 'FLX-SHIN-L-BLK'),
    ],
    attributes: attrs(['Loại', 'Bảo vệ ống đồng'], ['Trọng lượng', 'Nhẹ'], ['Điểm mạnh', 'Dễ mang, dễ vệ sinh']),
  },
  {
    id: 109,
    name: 'Túi đựng giày GoalStore Essentials',
    baseSku: 'ACC-BAG-009',
    brand: 'GoalStore',
    season: '2026',
    productType: 'Phụ kiện',
    targetGender: 'Unisex',
    material: 'Polyester',
    description: 'Túi đựng giày chống bám bẩn, có ngăn thoáng khí, tiện mang đi tập và thi đấu.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-9.jpg',
    categories: [categoryPhuKien],
    images: images('/legacy/img/product/product-9.jpg', '/legacy/img/shopping-cart/cart-3.jpg'),
    variants: [
      variant(1031, 'Tiêu chuẩn', 'Đen', 179000, null, 30, 'GS-BAG-STD-BLK'),
      variant(1032, 'Tiêu chuẩn', 'Xanh', 179000, null, 22, 'GS-BAG-STD-BLU'),
    ],
    attributes: attrs(['Loại', 'Túi đựng giày'], ['Ngăn chứa', '1 ngăn chính, 1 ngăn phụ'], ['Điểm mạnh', 'Gọn nhẹ']),
  },
  {
    id: 110,
    name: 'Bóng tập luyện size 5 Training Pro',
    baseSku: 'ACC-BALL-010',
    brand: 'Molten',
    season: '2026',
    productType: 'Phụ kiện',
    targetGender: 'Unisex',
    material: 'PU Composite',
    description: 'Bóng tập luyện size 5 độ nảy ổn định, phù hợp sân phủi và tập kỹ thuật cơ bản.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-10.jpg',
    categories: [categoryPhuKien, categoryTapLuyen],
    images: images('/legacy/img/product/product-10.jpg', '/legacy/img/shopping-cart/cart-4.jpg'),
    variants: [
      variant(1033, 'Size 5', 'Trắng', 420000, 369000, 14, 'MLT-BALL-5-WHT'),
      variant(1034, 'Size 5', 'Vàng', 420000, 369000, 10, 'MLT-BALL-5-YEL'),
    ],
    attributes: attrs(['Loại', 'Bóng tập luyện'], ['Chuẩn bóng', 'Size 5'], ['Điểm mạnh', 'Độ nảy ổn định']),
  },
  {
    id: 111,
    name: 'Áo bib chia đội Training Mesh',
    baseSku: 'ACC-BIB-011',
    brand: 'KeepFly',
    season: '2025',
    productType: 'Phụ kiện',
    targetGender: 'Unisex',
    material: 'Mesh',
    description: 'Áo bib lưới nhẹ, thoáng, dễ phân đội trong các buổi tập nhóm hoặc đá phủi.',
    releaseYear: 2025,
    thumbnailUrl: '/legacy/img/product/product-11.jpg',
    categories: [categoryTapLuyen, categoryPhuKien],
    images: images('/legacy/img/product/product-11.jpg', '/legacy/img/banner/banner-2.jpg'),
    variants: [
      variant(1035, 'Free size', 'Cam', 99000, null, 40, 'KPF-BIB-FS-ORG'),
      variant(1036, 'Free size', 'Xanh lá', 99000, null, 28, 'KPF-BIB-FS-GRN'),
    ],
    attributes: attrs(['Loại', 'Áo bib'], ['Mục đích', 'Chia đội'], ['Điểm mạnh', 'Thoáng, nhẹ']),
  },
  {
    id: 112,
    name: 'Nike Tiempo Legend 10 Club TF',
    baseSku: 'NIKE-TF-012',
    brand: 'Nike',
    season: '2026',
    productType: 'Giày',
    targetGender: 'Unisex',
    material: 'Synthetic Leather',
    description: 'Mẫu giày thiên kiểm soát, upper mềm, hợp người thích cảm giác chắc chân và chuyền bóng ổn định.',
    releaseYear: 2026,
    thumbnailUrl: '/legacy/img/product/product-12.jpg',
    categories: [categoryGiayCoNhanTao],
    images: images('/legacy/img/product/product-12.jpg', '/legacy/img/banner/banner-1.jpg'),
    variants: [
      variant(1037, '39', 'Đen', 1350000, 1190000, 9, 'NK-TIEMPO-39-BLK'),
      variant(1038, '40', 'Đen', 1350000, 1190000, 7, 'NK-TIEMPO-40-BLK'),
      variant(1039, '41', 'Trắng', 1350000, null, 4, 'NK-TIEMPO-41-WHT'),
      variant(1040, '42', 'Trắng', 1350000, null, 3, 'NK-TIEMPO-42-WHT'),
    ],
    attributes: attrs(['Mặt sân', 'Cỏ nhân tạo'], ['Phong cách', 'Kiểm soát bóng'], ['Điểm mạnh', 'Upper mềm']),
  },
]

export function toNumber(value: unknown) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

export function getVariantFinalPrice(variant?: ProductVariant | null) {
  if (!variant) return 0
  return toNumber(variant.salePrice) || toNumber(variant.price)
}

export function getSummaryFromDetail(product: PublicProductDetail): PublicProductSummary {
  const variants = product.variants || []
  const allPrices = variants.map((item) => toNumber(item.price)).filter((value) => value > 0)
  const allSalePrices = variants.map((item) => toNumber(item.salePrice)).filter((value) => value > 0)
  const stockTotal = variants.reduce((sum, item) => sum + toNumber(item.stockQuantity), 0)

  return {
    id: product.id,
    name: product.name,
    baseSku: product.baseSku,
    brand: product.brand,
    imageUrl: product.thumbnailUrl || product.images?.[0]?.imageUrl || undefined,
    price: allPrices[0] || 0,
    salePrice: allSalePrices[0] || null,
    minPrice: allPrices.length ? Math.min(...allPrices) : 0,
    maxPrice: allPrices.length ? Math.max(...allPrices) : 0,
    totalStock: stockTotal,
    stockStatus: stockTotal > 0 ? 'IN_STOCK' : 'OUT_OF_STOCK',
    productType: product.productType,
    targetGender: product.targetGender,
    categoryNames: (product.categories || []).map((item) => item.name),
  }
}

export const mockProductSummaries = mockProducts.map(getSummaryFromDetail)

export function findProductById(id: number | string) {
  return mockProducts.find((item) => item.id === Number(id)) || null
}

export function findVariantById(id: number | string) {
  for (const product of mockProducts) {
    const current = (product.variants || []).find((item) => item.id === Number(id))
    if (current) {
      return { product, variant: current }
    }
  }
  return null
}
