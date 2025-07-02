<template>
    <h2>帳號相關審核</h2>
    <main class="admin-main p-4">
        <div class="table-card mb-4">
            <!-- Filter Bar -->
            <div class="filter-bar mb-4 d-flex flex-wrap align-items-center gap-2">
                <div>搜尋：</div>
                <input v-model="keyword" placeholder="搜尋帳號名稱..." class="form-control w-auto" />
                身分：
                <select v-model="selectedIdentity" class="form-select" style="width: 120px;">
                    <option value="">全部</option>
                    <option value="商家">商家</option>
                    <option value="使用者">使用者</option>
                </select>
                <button class="btn btn-primary" @click="resetFilters">清除篩選</button>
            </div>
            <!-- 狀態 Tab -->
            <div class="mb-2 d-flex gap-2">
                <button class="btn btn-link"
                    :class="{ 'tab-active': currentFilter === 'all' }"
                    @click="setFilter('all')">全部 ({{ countAll }})</button>
                <button class="btn btn-link"
                    :class="{ 'tab-active': currentFilter === 'pending' }"
                    @click="setFilter('pending')">待審核 ({{ countPending }})</button>
                <button class="btn btn-link"
                    :class="{ 'tab-active': currentFilter === 'approved' }"
                    @click="setFilter('approved')">已審核 ({{ countApproved }})</button>
            </div>
            <!-- 批次選擇 -->
            <div class="mb-3">
                <input type="checkbox" :checked="isAllSelected" @change="toggleAll"> 全部勾選
                <button class="btn btn-primary btn-sm ms-2 me-2" @click="batchApprove">批次通過</button>
                <button class="btn btn-danger btn-sm" @click="batchReject">批次駁回</button>
            </div>

            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th style="width:40px"></th>
                        <th>帳號身分</th>
                        <th>帳號名稱</th>
                        <th>審核內容</th>
                        <th>審核狀態</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="account in pagedAccounts" :key="account.id">
                        <td><input type="checkbox" v-model="selected" :value="account.id"></td>
                        <td>{{ account.identity }}</td>
                        <td>{{ account.name }}</td>
                        <td>{{ account.content }}</td>
                        <td>
                            <span v-if="account.status === '待審核'" class="badge bg-warning text-dark">{{ account.status }}</span>
                            <span v-else-if="account.status.includes('已')" class="badge bg-success">{{ account.status }}</span>
                            <span v-else>{{ account.status }}</span>
                            <br v-if="account.detail">
                            <small v-if="account.detail">{{ account.detail }}</small>
                        </td>
                        <td>
                            <button class="btn btn-success btn-sm me-1" @click="confirmAccount(account.id)">確認</button>
                            <button class="btn btn-danger btn-sm" @click="rejectAccount(account.id)">拒絕</button>
                        </td>
                    </tr>
                    <!-- 查無資料 -->
                    <tr v-if="pagedAccounts.length === 0">
                        <td colspan="6" class="text-center">查無資料</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <!-- 分頁區塊 -->
        <div class="pagination d-flex justify-content-end align-items-center pagebar-wrap">
            <button class="btn btn-outline-secondary me-2"
                :disabled="page === 1"
                @click="page > 1 && (page--)"
            >&lt; 上一頁</button>
            <nav>
                <ul class="pagination mb-0">
                    <li class="page-item disabled">
                        <span class="page-link">頁數：{{ page }} / {{ totalPages }}</span>
                    </li>
                </ul>
            </nav>
            <button class="btn btn-outline-secondary ms-2"
                :disabled="page === totalPages"
                @click="page < totalPages && (page++)"
            >下一頁 &gt;</button>
            <div class="ms-3">
                <select class="form-select" v-model.number="pageSize" style="width:120px;min-width:90px;">
                    <option v-for="s in pageSizes" :key="s" :value="s">{{ s }}/每頁</option>
                </select>
            </div>
        </div>
    </main>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const accounts = ref([])

const fetchAccounts = async () => {
    try {
        const res = await axios.get('http://localhost:8080/api/accounts')
        accounts.value = res.data
    } catch (err) {
        accounts.value = []
    }
}

// 關鍵字、身分過濾
const keyword = ref('')
const selectedIdentity = ref('')

// 狀態過濾
const currentFilter = ref(route.query.filter || 'all')

// 分頁
const page = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 30]

const filteredAccounts = computed(() => {
    let result = accounts.value
    if (currentFilter.value === 'pending') {
        result = result.filter(a => a.status.includes('待'))
    } else if (currentFilter.value === 'approved') {
        result = result.filter(a => a.status.includes('已'))
    }
    // 關鍵字
    if (keyword.value) {
        result = result.filter(a => a.name.includes(keyword.value))
    }
    // 身分
    if (selectedIdentity.value) {
        result = result.filter(a => a.identity === selectedIdentity.value)
    }
    return result
})

const pagedAccounts = computed(() => {
    const start = (page.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filteredAccounts.value.slice(start, end)
})

const totalPages = computed(() =>
    Math.max(1, Math.ceil(filteredAccounts.value.length / pageSize.value))
)

// 狀態 Tab 計算
const countAll = computed(() => accounts.value.length)
const countPending = computed(() => accounts.value.filter(a => a.status.includes('待')).length)
const countApproved = computed(() => accounts.value.filter(a => a.status.includes('已')).length)

const selectAll = ref(false)
const selected = ref([])

const setFilter = (filter) => {
    if (route.query.filter === filter) return
    router.replace({ query: { filter } })
    selected.value = []
    page.value = 1
}

const toggleAll = () => {
    if (isAllSelected.value) {
        selected.value = []
    } else {
        selected.value = pagedAccounts.value.map(account => account.id)
    }
}
const isAllSelected = computed(() =>
    selected.value.length === pagedAccounts.value.length && pagedAccounts.value.length !== 0
)
const batchApprove = () => {
    alert('批次通過: ' + selected.value.join(', '))
    selected.value = []
}
const batchReject = () => {
    alert('批次駁回: ' + selected.value.join(', '))
    selected.value = []
}
watch(selected, () => {
    selectAll.value = selected.value.length === pagedAccounts.value.length && pagedAccounts.value.length !== 0
})
watch([page, pageSize], () => {
    selected.value = []
    selectAll.value = false
})
watch(pageSize, () => { page.value = 1 })

function resetFilters() {
    keyword.value = ''
    selectedIdentity.value = ''
}

const rejectAccount = async (id) => {
    try {
        alert(`已拒絕帳號 ID: ${id}`)
        fetchAccounts()
    } catch (err) {
        alert('拒絕失敗')
    }
}
const confirmAccount = (id) => {
    alert(`已確認帳號 ID: ${id}`)
    // 你可以加 API call
}

onMounted(() => {
    // 可換 fetchAccounts()
    accounts.value = [
        { id:1, identity:'商家', name:'張三餐飲有限公司',content:'被舉報餐廳環境髒亂' ,status:'待審核', detail:'待商家進一步提出處理方案' },
        { id:2, identity:'使用者', name:'李四', content:'多次不拿餐點' ,  status:'已停權', detail:'2025-06-01 14:23' }
    ]
})
</script>