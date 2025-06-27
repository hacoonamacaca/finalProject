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

const selectAll = ref(false)
const selected = ref([])

const currentFilter = ref(route.query.filter || 'all')

// ----------- 分頁相關 -----------
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
    return result
})

// 分頁後的資料
const pagedAccounts = computed(() => {
    const start = (page.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filteredAccounts.value.slice(start, end)
})

const totalPages = computed(() =>
    Math.max(1, Math.ceil(filteredAccounts.value.length / pageSize.value))
)

watch(() => route.query.filter, (val) => {
    if (val === 'pending' || val === 'approved' || val === 'all') {
        currentFilter.value = val
    } else {
        currentFilter.value = 'all'
    }
    page.value = 1 // 切換 filter 時回到第1頁
})

// onMounted(() => {
//     fetchAccounts()
//     if (!route.query.filter) {
//         router.replace({ query: { filter: 'all' } })
//         currentFilter.value = 'all'
//     }
// })

    onMounted(() => {
    accounts.value = [
        { id:1, identity:'商家', name:'張三餐飲有限公司', status:'待審核', detail:'' },
        { id:2, identity:'商家', name:'李四小吃店',     status:'已通過',   detail:'2025-06-01 14:23' },
        { id:3, identity:'合作夥伴', name:'王五食品行', status:'需修改',   detail:'請補充營業執照影本' },
        { id:4, identity:'商家', name:'趙六飲料吧',     status:'已通過',   detail:'2025-05-28 09:11' },
        { id:5, identity:'合作夥伴', name:'周七雜貨店', status:'待審核',   detail:'' }
    ]
    })

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

const countAll = computed(() => accounts.value.length)
const countPending = computed(() => accounts.value.filter(a => a.status.includes('待')).length)
const countApproved = computed(() => accounts.value.filter(a => a.status.includes('已')).length)

watch(selected, () => {
    selectAll.value = selected.value.length === pagedAccounts.value.length && pagedAccounts.value.length !== 0
})
watch([page, pageSize], () => {
    // 換頁或換每頁數時要自動清空全選與選取
    selected.value = []
    selectAll.value = false
})

// 換每頁數量時，頁碼也回到第1頁
watch(pageSize, () => {
    page.value = 1
})
const rejectAccount = async (id) => {
    try {
        // TODO: 換成你的 API 路徑
        // await axios.post(`/api/accounts/${id}/reject`)
        alert(`已拒絕帳號 ID: ${id}`)
        // 重新讀取或更新狀態
        fetchAccounts()
    } catch (err) {
        console.error(err)
        alert('拒絕失敗')
    }
}

</script>

<template>
    <div class="container mt-4">
        <h2>帳號相關審核</h2>
        <!-- 狀態選單 -->
        <div class="d-flex mb-3 border-bottom pb-2">
            <button class="btn btn-link"
                :class="{ 'tab-active': currentFilter === 'all' }"
                @click="setFilter('all')"
            >全部 ({{ countAll }})</button>
            <button class="btn btn-link"
                :class="{ 'tab-active': currentFilter === 'pending' }"
                @click="setFilter('pending')"
            >待審核 ({{ countPending }})</button>
            <button class="btn btn-link"
                :class="{ 'tab-active': currentFilter === 'approved' }"
                @click="setFilter('approved')"
            >已審核 ({{ countApproved }})</button>
        </div>

        <div class="mb-2">
            <input type="checkbox" :checked="isAllSelected" @change="toggleAll"> 全部勾選
            <button class="btn btn-primary me-2" @click="batchApprove">批次通過</button>
            <button class="btn btn-danger" @click="batchReject">批次駁回</button>
        </div>

        <table class="table table-bordered">
            <thead>
                <tr>
                    <th></th>
                    <th>帳號身分</th>
                    <th>帳號名稱</th>
                    <th>審核狀態</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="account in pagedAccounts" :key="account.id">
                    <td><input type="checkbox" v-model="selected" :value="account.id"></td>
                    <td>{{ account.identity }}</td>
                    <td>{{ account.name }}</td>
                    <td :class="{ 'text-warning': account.status === '需修改', 'text-success': account.status === '已通過' }">
                        {{ account.status }}
                        <br v-if="account.detail">
                        <small v-if="account.detail">{{ account.detail }}</small>
                    </td>
                    <td>
                        <!-- 新增:確認與拒絕 -->
                        <button class="btn btn-success btn-sm me-1"
                        @click="confirmAccount(account.id)"
                        >確認
                        </button>
                        <button class="btn btn-danger btn-sm me-1"
                        @click="rejectAccount(account.id)"
                        >拒絕
                        </button>
                    </td>
                </tr>
            <!-- 查無資料 -->
            <tr v-if="pagedAccounts.length === 0">
                <td colspan="6" class="text-center">查無資料</td>
            </tr>
            </tbody>
        </table>
        <!-- 分頁控制 -->
        <div class="d-flex justify-content-end align-items-center pagebar-wrap">
            <nav>
                <ul class="pagination mb-0">
                    <li class="page-item" :class="{ disabled: page === 1 }">
                        <button class="page-link" @click="page > 1 && (page--)" :disabled="page === 1">‹</button>
                    </li>
                    <li class="page-item disabled"><span class="page-link">{{ page }}</span></li>
                    <li class="page-item" :class="{ disabled: page === totalPages }">
                        <button class="page-link" @click="page < totalPages && (page++)" :disabled="page === totalPages">›</button>
                    </li>
                </ul>
            </nav>
            <div class="ms-3">
                <select class="form-select" v-model.number="pageSize">
                    <option v-for="s in pageSizes" :key="s" :value="s">{{ s }}/每頁</option>
                </select>
            </div>
        </div>
    </div>
</template>