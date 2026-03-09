<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-semibold">Module Product</h1>
      <p class="text-slate-600">Folder riêng cho người làm Product/Category. Các file trong module này có thể sửa độc lập.</p>
    </div>

    <div class="grid md:grid-cols-2 gap-4">
      <div class="rounded-xl border bg-white p-4">
        <h2 class="font-semibold mb-2">Việc nên để trong module này</h2>
        <ul class="list-disc pl-5 text-sm text-slate-600 space-y-1">
          <li>Danh sách sản phẩm</li>
          <li>Form thêm/sửa sản phẩm</li>
          <li>Category, image, variant</li>
          <li>API product/category riêng</li>
        </ul>
      </div>
      <div class="rounded-xl border bg-white p-4">
        <h2 class="font-semibold mb-2">Không nên sửa nhiều</h2>
        <ul class="list-disc pl-5 text-sm text-slate-600 space-y-1">
          <li>Router chung</li>
          <li>MainLayout</li>
          <li>api.ts dùng chung</li>
        </ul>
      </div>
    </div>
  </div>
</template>
