<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const restaurants = ref([])

const fetchRestaurants = async () => {
    try {
        const res = await axios.get('http://localhost:8080/api/restaurants')
        restaurants.value = res.data
    } catch (err) {
        restaurants.value = []
    }
}

const selectAll = ref(false)
const selected = ref([])

const currentFilter = ref(route.query.filter || 'all')

// ----------- 分頁相關 -----------
const page = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 30]

const filteredRestaurants = computed(() => {
    let result = restaurants.value
    if (currentFilter.value === 'pending') {
        result = result.filter(r => r.status.includes('待'))
    } else if (currentFilter.value === 'approved') {
        result = result.filter(r => r.status.includes('已'))
    }
    return result
})

// 分頁後的資料
const pagedRestaurants = computed(() => {
    const start = (page.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filteredRestaurants.value.slice(start, end)
})

const totalPages = computed(() =>
    Math.max(1, Math.ceil(filteredRestaurants.value.length / pageSize.value))
)

watch(() => route.query.filter, (val) => {
    if (val === 'pending' || val === 'approved' || val === 'all') {
        currentFilter.value = val
    } else {
        currentFilter.value = 'all'
    }
    page.value = 1 // 切換 filter 時回到第1頁
})

onMounted(() => {
    fetchRestaurants()
    if (!route.query.filter) {
        router.replace({ query: { filter: 'all' } })
        currentFilter.value = 'all'
    }
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
        selected.value = pagedRestaurants.value.map(restaurant => restaurant.id)
    }
}
const isAllSelected = computed(() =>
    selected.value.length === pagedRestaurants.value.length && pagedRestaurants.value.length !== 0
)

const batchApprove = () => {
    alert('批次通過: ' + selected.value.join(', '))
    selected.value = []
}
const batchReject = () => {
    alert('批次駁回: ' + selected.value.join(', '))
    selected.value = []
}

const countAll = computed(() => restaurants.value.length)
const countPending = computed(() => restaurants.value.filter(a => a.status.includes('待')).length)
const countApproved = computed(() => restaurants.value.filter(a => a.status.includes('已')).length)

watch(selected, () => {
    selectAll.value = selected.value.length === pagedRestaurants.value.length && pagedRestaurants.value.length !== 0
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

</script>

<template>
    <div class="container mt-4">
        <h2>餐廳審核</h2>
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
                    <th>餐廳名稱</th>
                    <th>餐廳負責人</th>
                    <th>審核狀態</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="restaurant in pagedRestaurants" :key="restaurant.id">
                    <td><input type="checkbox" v-model="selected" :value="restaurant.id"></td>
                    <td>{{ restaurant.name }}</td>
                    <td>{{ restaurant.manager }}</td>
                    <td :class="{ 'text-warning': restaurant.status === '需修改', 'text-success': restaurant.status === '已通過' }">
                        {{ restaurant.status }}
                        <br v-if="restaurant.detail">
                        <small v-if="restaurant.detail">{{ restaurant.detail }}</small>
                    </td>
                    <td>
                        <a href="#">編輯</a> |
                        <a href="#">查看</a>
                    </td>
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