<template>
  <div class="space-y-5 p-5">
    <div class="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-slate-800">Quản lý khách hàng</h1>
        <p class="text-sm text-slate-500">
          Danh sách, chi tiết, lịch sử giao dịch, top chi tiêu và khách hàng lâu chưa mua
        </p>
      </div>
    </div>

    <CustomerStatsCards :stats="stats" />

    <CustomerFilterBar
      :model-value="filter"
      @apply="applyFilter"
      @create="openCreate"
    />

    <div class="flex flex-wrap gap-2">
      <button
        type="button"
        class="rounded-xl px-4 py-2 text-sm font-semibold"
        :class="activeTab === 'list' ? activeClass : inactiveClass"
        @click="activeTab = 'list'"
      >
        Danh sách khách hàng
      </button>

      <button
        type="button"
        class="rounded-xl px-4 py-2 text-sm font-semibold"
        :class="activeTab === 'spending' ? activeClass : inactiveClass"
        @click="activeTab = 'spending'"
      >
        Theo mức chi tiêu
      </button>

      <button
        type="button"
        class="rounded-xl px-4 py-2 text-sm font-semibold"
        :class="activeTab === 'inactive' ? activeClass : inactiveClass"
        @click="activeTab = 'inactive'"
      >
        Lâu chưa mua
      </button>

      <button
        v-if="selectedCustomer"
        type="button"
        class="rounded-xl px-4 py-2 text-sm font-semibold"
        :class="activeTab === 'detail' ? activeClass : inactiveClass"
        @click="activeTab = 'detail'"
      >
        Chi tiết khách hàng
      </button>
    </div>

    <CustomerTable
      v-if="activeTab === 'list'"
      :customers="filteredCustomers"
      @view="openDetail"
      @edit="openEdit"
      @delete="handleDelete"
      @assign-vip="handleAssignVip"
    />

    <SpendingCustomerTable
      v-else-if="activeTab === 'spending'"
      :customers="spendingCustomers"
    />

    <InactiveCustomerTable
      v-else-if="activeTab === 'inactive'"
      :customers="inactiveCustomers"
    />

    <div
      v-else-if="activeTab === 'detail' && selectedCustomer"
      class="space-y-5"
    >
      <CustomerDetailCard :customer="selectedCustomer" />
      <CustomerTransactionTable :transactions="transactions" />
    </div>

    <div
      v-if="showForm"
      class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4"
    >
      <div class="w-full max-w-3xl">
        <CustomerForm
          :model-value="selectedFormCustomer"
          @save="handleSave"
          @cancel="closeForm"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";

import CustomerFilterBar from "../components/CustomerFilterBar.vue";
import CustomerForm from "../components/CustomerForm.vue";
import CustomerTable from "../components/CustomerTable.vue";
import CustomerDetailCard from "../components/CustomerDetailCard.vue";
import CustomerTransactionTable from "../components/CustomerTransactionTable.vue";
import CustomerStatsCards from "../components/CustomerStatsCards.vue";
import InactiveCustomerTable from "../components/InactiveCustomerTable.vue";
import SpendingCustomerTable from "../components/SpendingCustomerTable.vue";

import type { Customer } from "../types/customer";
import { defaultCustomer } from "../types/customer";
import type { CustomerFilter } from "../types/customerFilter";
import type { CustomerStats } from "../types/customerStats";
import type { CustomerTransaction } from "../types/customerTransaction";

import {
  advancedFilterCustomers,
  assignVip,
  createCustomer,
  deleteCustomer,
  getCustomerDetail,
  getCustomerStats,
  getCustomers,
  getInactiveCustomers,
  getTopSpendingCustomers,
  updateCustomer,
} from "../services/customerService";
import { getTransactionsByCustomer } from "../services/customerTransactionService";

type CustomerTab = "list" | "spending" | "inactive" | "detail";

const activeTab = ref<CustomerTab>("list");
const customers = ref<Customer[]>([]);
const filteredCustomers = ref<Customer[]>([]);
const spendingCustomers = ref<Customer[]>([]);
const inactiveCustomers = ref<Customer[]>([]);
const transactions = ref<CustomerTransaction[]>([]);

const selectedCustomer = ref<Customer | null>(null);
const selectedFormCustomer = ref<Customer>({ ...defaultCustomer });
const showForm = ref(false);

const filter = ref<CustomerFilter>({
  keyword: "",
  loai_khach: "",
  inactiveDays: 0,
  minPoints: null,
  maxPoints: null,
  minSpending: null,
  maxSpending: null,
  sortBy: "newest",
});

const stats = ref<CustomerStats>({
  totalCustomers: 0,
  totalVipCustomers: 0,
  totalRegularCustomers: 0,
  totalPoints: 0,
  totalRevenueFromCustomers: 0,
  active30Days: 0,
});

const activeClass = "bg-emerald-600 text-white";
const inactiveClass =
  "border border-slate-300 bg-white text-slate-700 hover:bg-slate-50";

onMounted(async () => {
  await loadInitialData();
});

async function loadInitialData() {
  await Promise.all([
    loadCustomers(),
    loadStats(),
    loadTopSpending(),
    loadInactiveCustomers(),
  ]);
}

async function loadCustomers() {
  try {
    const data = await getCustomers();
    customers.value = data;
    filteredCustomers.value = applyClientSort([...data], filter.value.sortBy);
  } catch (error: any) {
  console.error("Lỗi load customer:", error);
  customers.value = [];
  filteredCustomers.value = [];

  const message =
    error?.response?.data?.message ||
    error?.message ||
    "Không tải được danh sách khách hàng";

  alert(message);
  }
}

async function loadStats() {
  try {
    stats.value = await getCustomerStats();
  } catch (error) {
    console.error("Lỗi load stats:", error);
  }
}

async function loadTopSpending() {
  try {
    spendingCustomers.value = await getTopSpendingCustomers();
  } catch (error) {
    console.error("Lỗi load top spending:", error);
    spendingCustomers.value = [];
  }
}

async function loadInactiveCustomers() {
  try {
    const days = filter.value.inactiveDays > 0 ? filter.value.inactiveDays : 30;
    inactiveCustomers.value = await getInactiveCustomers(days);
  } catch (error) {
    console.error("Lỗi load inactive customers:", error);
    inactiveCustomers.value = [];
  }
}

function applyClientSort(list: Customer[], sortBy: CustomerFilter["sortBy"]) {
  switch (sortBy) {
    case "name":
      return list.sort((a, b) => a.ten.localeCompare(b.ten, "vi"));
    case "points-desc":
      return list.sort((a, b) => (b.diem_tich_luy || 0) - (a.diem_tich_luy || 0));
    case "spending-desc":
      return list.sort((a, b) => (b.tong_chi_tieu || 0) - (a.tong_chi_tieu || 0));
    case "inactive-desc":
      return list.sort((a, b) => (b.so_ngay_khong_mua || 0) - (a.so_ngay_khong_mua || 0));
    default:
      return list.sort((a, b) => (b.id || 0) - (a.id || 0));
  }
}

async function applyFilter(value: CustomerFilter) {
  filter.value = { ...value };

  try {
    filteredCustomers.value = await advancedFilterCustomers(filter.value);
    await loadInactiveCustomers();
  } catch (error) {
    console.error("Lỗi filter customer:", error);
    alert("Lọc khách hàng thất bại");
  }
}

function openCreate() {
  selectedFormCustomer.value = { ...defaultCustomer };
  showForm.value = true;
}

function openEdit(customer: Customer) {
  selectedFormCustomer.value = { ...customer };
  showForm.value = true;
}

function closeForm() {
  showForm.value = false;
  selectedFormCustomer.value = { ...defaultCustomer };
}

async function handleSave(customer: Customer) {
  try {
    if (customer.id) {
      await updateCustomer(customer.id, customer);
    } else {
      await createCustomer(customer);
    }

    showForm.value = false;
    selectedFormCustomer.value = { ...defaultCustomer };
    await loadInitialData();

    if (filter.value.keyword || filter.value.loai_khach || filter.value.inactiveDays) {
      await applyFilter(filter.value);
    }
  } catch (error) {
    console.error("Lỗi save customer:", error);
    alert("Lưu khách hàng thất bại");
  }
}

async function handleDelete(id?: number) {
  if (!id) return;

  const ok = window.confirm("Xác nhận xóa khách hàng này?");
  if (!ok) return;

  try {
    await deleteCustomer(id);

    if (selectedCustomer.value?.id === id) {
      selectedCustomer.value = null;
      transactions.value = [];
      activeTab.value = "list";
    }

    await loadInitialData();

    if (filter.value.keyword || filter.value.loai_khach || filter.value.inactiveDays) {
      await applyFilter(filter.value);
    }
  } catch (error: any) {
    console.error("Lỗi delete customer:", error);
    alert(error?.response?.data || "Xóa khách hàng thất bại");
  }
}

async function openDetail(customer: Customer) {
  activeTab.value = "detail";

  try {
    if (!customer.id) {
      selectedCustomer.value = { ...customer };
      transactions.value = [];
      return;
    }

    const [detail, orderList] = await Promise.all([
      getCustomerDetail(customer.id),
      getTransactionsByCustomer(customer.id),
    ]);

    selectedCustomer.value = { ...detail };
    transactions.value = orderList;
  } catch (error) {
    console.error("Lỗi load detail customer:", error);
    selectedCustomer.value = { ...customer };
    transactions.value = [];
  }
}

async function handleAssignVip(customer: Customer) {
  if (!customer.id) return;

  try {
    await assignVip(customer.id);
    await loadInitialData();

    if (selectedCustomer.value?.id === customer.id) {
      await openDetail({ ...customer, loai_khach: "VIP" });
    }

    if (filter.value.keyword || filter.value.loai_khach || filter.value.inactiveDays) {
      await applyFilter(filter.value);
    }
  } catch (error) {
    console.error("Lỗi gán VIP:", error);
    alert("Gán VIP thất bại");
  }
}
</script>