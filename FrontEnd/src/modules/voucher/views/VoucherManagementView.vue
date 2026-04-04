<template>
  <section class="space-y-6">
    <div class="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 class="text-2xl font-semibold text-slate-900">Quản lý voucher</h1>
        <p class="text-sm text-slate-500">Admin tạo, sửa, bật tắt và xóa mềm voucher cho bên bán hàng online.</p>
      </div>
      <button
        class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-white"
        @click="startCreate"
      >
        Tạo voucher mới
      </button>
    </div>

    <div v-if="notice" class="rounded-md border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
      {{ notice }}
    </div>
    <div v-if="error" class="rounded-md border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
      {{ error }}
    </div>

    <div class="grid grid-cols-1 gap-6 xl:grid-cols-[1.2fr,0.8fr]">
      <div class="rounded-xl border bg-white">
        <div class="flex items-center justify-between border-b px-4 py-3">
          <div>
            <h2 class="font-semibold text-slate-900">Danh sách voucher</h2>
            <p class="text-xs text-slate-500">Mỗi voucher có thể được khách hàng nhận 1 lần và có số lượng phát hành giới hạn.</p>
          </div>
          <div class="text-sm text-slate-500">Tổng: {{ vouchers.length }}</div>
        </div>

        <div class="overflow-x-auto">
          <table class="min-w-full text-sm">
            <thead class="bg-slate-50 text-left text-slate-500">
              <tr>
                <th class="px-4 py-3 font-medium">Mã</th>
                <th class="px-4 py-3 font-medium">Tên</th>
                <th class="px-4 py-3 font-medium">Giảm</th>
                <th class="px-4 py-3 font-medium">Đơn tối thiểu</th>
                <th class="px-4 py-3 font-medium">Số lượng</th>
                <th class="px-4 py-3 font-medium">Trạng thái</th>
                <th class="px-4 py-3 font-medium">Thống kê</th>
                <th class="px-4 py-3 font-medium">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="8" class="px-4 py-6 text-center text-slate-500">Đang tải voucher...</td>
              </tr>
              <tr v-else-if="!vouchers.length">
                <td colspan="8" class="px-4 py-6 text-center text-slate-500">Chưa có voucher nào.</td>
              </tr>
              <tr v-for="item in vouchers" :key="item.id" class="border-t align-top">
                <td class="px-4 py-3 font-semibold text-slate-900">{{ item.code }}</td>
                <td class="px-4 py-3">
                  <div class="font-medium text-slate-900">{{ item.name }}</div>
                  <div class="mt-1 max-w-xs text-xs text-slate-500">{{ item.description || 'Không có mô tả' }}</div>
                </td>
                <td class="px-4 py-3">{{ item.discountPercent }}%</td>
                <td class="px-4 py-3">{{ formatCurrency(item.minOrderAmount) }}</td>
                <td class="px-4 py-3 text-xs text-slate-600">
                  <div>Tổng: <strong>{{ item.totalQuantity || 0 }}</strong></div>
                  <div>Còn lại: <strong>{{ item.remainingQuantity || 0 }}</strong></div>
                </td>
                <td class="px-4 py-3">
                  <span class="inline-flex rounded-full border px-2.5 py-1 text-xs font-medium" :class="item.isActive ? 'border-emerald-200 bg-emerald-50 text-emerald-700' : 'border-slate-200 bg-slate-100 text-slate-600'">
                    {{ item.isActive ? 'Đang bật' : 'Đang tắt' }}
                  </span>
                </td>
                <td class="px-4 py-3 text-xs text-slate-600">
                  <div>Đã nhận: <strong>{{ item.claimedCount || 0 }}</strong></div>
                  <div>Đã dùng: <strong>{{ item.usedCount || 0 }}</strong></div>
                </td>
                <td class="px-4 py-3">
                  <div class="flex flex-wrap gap-2">
                    <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-slate-50" @click="startEdit(item)">Sửa</button>
                    <button
                      class="rounded-md border px-3 py-1.5 text-xs font-medium"
                      :class="item.isActive ? 'border-amber-200 text-amber-700 hover:bg-amber-50' : 'border-emerald-200 text-emerald-700 hover:bg-emerald-50'"
                      @click="toggleStatus(item)"
                    >
                      {{ item.isActive ? 'Tắt' : 'Bật' }}
                    </button>
                    <button class="rounded-md border border-rose-200 px-3 py-1.5 text-xs font-medium text-rose-700 hover:bg-rose-50" @click="handleDelete(item)">Xóa mềm</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="rounded-xl border bg-white">
        <div class="border-b px-4 py-3">
          <h2 class="font-semibold text-slate-900">{{ editingId ? 'Cập nhật voucher' : 'Tạo voucher mới' }}</h2>
          <p class="text-xs text-slate-500">Tạo các voucher như GOAL10, GOAL20, GOAL30... để khách tự vào nhận.</p>
        </div>

        <div class="space-y-4 p-4">
          <label class="block space-y-1">
            <span class="text-sm font-medium text-slate-700">Mã voucher</span>
            <input v-model.trim="form.code" type="text" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" placeholder="Ví dụ: GOAL10" />
          </label>

          <label class="block space-y-1">
            <span class="text-sm font-medium text-slate-700">Tên voucher</span>
            <input v-model.trim="form.name" type="text" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" placeholder="Ví dụ: Voucher giảm 10%" />
          </label>

          <label class="block space-y-1">
            <span class="text-sm font-medium text-slate-700">Mô tả</span>
            <textarea v-model.trim="form.description" rows="4" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" placeholder="Mô tả ngắn về voucher"></textarea>
          </label>

          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <label class="block space-y-1">
              <span class="text-sm font-medium text-slate-700">Phần trăm giảm</span>
              <input v-model.number="form.discountPercent" type="number" min="1" max="100" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" />
            </label>

            <label class="block space-y-1">
              <span class="text-sm font-medium text-slate-700">Đơn tối thiểu</span>
              <input v-model.number="form.minOrderAmount" type="number" min="0" step="1000" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" />
            </label>
          </div>

          <label class="block space-y-1">
            <span class="text-sm font-medium text-slate-700">Số lượng phát hành</span>
            <input v-model.number="form.totalQuantity" type="number" min="1" class="w-full rounded-md border px-3 py-2 outline-none focus:border-primary" />
            <span class="text-xs text-slate-500">Mỗi khách chỉ nhận 1 lần, và tổng số người nhận không vượt quá số lượng này.</span>
          </label>

          <label class="flex items-center gap-2 rounded-md border px-3 py-2 text-sm text-slate-700">
            <input v-model="form.isActive" type="checkbox" />
            <span>Bật voucher ngay sau khi lưu</span>
          </label>

          <div class="flex flex-wrap gap-3 pt-2">
            <button class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-white disabled:opacity-60" :disabled="saving" @click="handleSubmit">
              {{ saving ? 'Đang lưu...' : editingId ? 'Lưu cập nhật' : 'Tạo voucher' }}
            </button>
            <button class="rounded-md border px-4 py-2 text-sm font-medium hover:bg-slate-50" :disabled="saving" @click="resetForm">
              Làm mới
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createVoucher, deleteVoucher, getVouchers, updateVoucher, updateVoucherStatus } from '../services/voucherApi'
import type { VoucherItem } from '../types/voucher'

const loading = ref(false)
const saving = ref(false)
const vouchers = ref<VoucherItem[]>([])
const editingId = ref<number | null>(null)
const notice = ref('')
const error = ref('')

const form = reactive({
  code: '',
  name: '',
  description: '',
  discountPercent: 10,
  minOrderAmount: 0,
  totalQuantity: 100,
  isActive: true,
})

function formatCurrency(value: unknown) {
  const amount = Number(value || 0)
  return `${amount.toLocaleString('vi-VN')}₫`
}

function resetMessages() {
  notice.value = ''
  error.value = ''
}

function resetForm() {
  editingId.value = null
  form.code = ''
  form.name = ''
  form.description = ''
  form.discountPercent = 10
  form.minOrderAmount = 0
  form.totalQuantity = 100
  form.isActive = true
}

function startCreate() {
  resetMessages()
  resetForm()
}

function startEdit(item: VoucherItem) {
  resetMessages()
  editingId.value = item.id
  form.code = item.code || ''
  form.name = item.name || ''
  form.description = item.description || ''
  form.discountPercent = Number(item.discountPercent || 0)
  form.minOrderAmount = Number(item.minOrderAmount || 0)
  form.totalQuantity = Number(item.totalQuantity || 0)
  form.isActive = Boolean(item.isActive)
}

async function loadData() {
  loading.value = true
  resetMessages()
  try {
    vouchers.value = await getVouchers()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể tải danh sách voucher.'
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  resetMessages()
  saving.value = true
  try {
    const payload = {
      code: form.code,
      name: form.name,
      description: form.description,
      discountPercent: Number(form.discountPercent || 0),
      minOrderAmount: Number(form.minOrderAmount || 0),
      totalQuantity: Number(form.totalQuantity || 0),
      isActive: Boolean(form.isActive),
    }

    if (editingId.value) {
      await updateVoucher(editingId.value, payload)
      notice.value = 'Cập nhật voucher thành công.'
    } else {
      await createVoucher(payload)
      notice.value = 'Tạo voucher thành công.'
    }

    resetForm()
    await loadData()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể lưu voucher.'
  } finally {
    saving.value = false
  }
}

async function toggleStatus(item: VoucherItem) {
  resetMessages()
  try {
    await updateVoucherStatus(item.id, !item.isActive)
    notice.value = item.isActive ? 'Đã tắt voucher.' : 'Đã bật voucher.'
    await loadData()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể cập nhật trạng thái voucher.'
  }
}

async function handleDelete(item: VoucherItem) {
  const confirmed = window.confirm(`Xóa mềm voucher ${item.code}?`)
  if (!confirmed) return

  resetMessages()
  try {
    await deleteVoucher(item.id)
    notice.value = 'Đã xóa mềm voucher.'
    if (editingId.value === item.id) {
      resetForm()
    }
    await loadData()
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || 'Không thể xóa voucher.'
  }
}

onMounted(loadData)
</script>
