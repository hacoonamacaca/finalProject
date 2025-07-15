<template>
    <h2>優惠活動</h2>
    <div v-if="sidebarOpen" class="overlay" @click="toggleSidebar"></div>
    <main class="admin-main p-4">
        <div class="table-card mb-4">
        <div class="filter-bar mb-4 d-flex flex-wrap align-items-center gap-2">
            <div>搜尋：</div>
            <input v-model="keyword" placeholder="搜尋活動標題..." class="form-control w-auto" /> 折扣類型：
            <select v-model="selectedType" class="form-select" style="width: 150px;">
            <option value="">全部</option>
            <option value="percentage">百分比</option>
            <option value="fixed">金額</option>
            </select>
            <button class="btn btn-primary" @click="resetFilters">清除篩選</button>
        </div>
        <table class="table table-striped table-hover promotion-table">
            <thead>
            <tr>
                <th>活動標題</th>
                <th>優惠內容</th>
                <th>起訖</th>
                <th>折扣類型</th>
                <th>門檻</th>
                <th>優惠碼</th>
                <th>使用上限</th>
                <th>每人上限</th>
                <th>餐廳條件</th>
                <th>食物條件</th>
                <th>會員條件</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="promotion in paginatedPromotions" :key="promotion.id">
            <!-- 編輯中：顯示 input -->
            <template v-if="editingId === promotion.id">
                <!-- 標題 -->
                <td><input v-model="editedPromotion.title" class="form-control form-control-sm" /></td>
                <!-- 優惠內容 -->
                <td><input v-model="editedPromotion.description" class="form-control form-control-sm" /></td>
                <!-- 優惠時間 -->
                <td>
                <input type="datetime-local" v-model="editedPromotion.startTime" class="form-control form-control-sm mb-1" />
                <input type="datetime-local" v-model="editedPromotion.endTime" class="form-control form-control-sm" />
                </td>
                <!-- 優惠類型 -->
                <td><input v-model="editedPromotion.discountType" class="form-control form-control-sm" /></td>
                <!-- 消費門檻 -->
                <td><input v-model.number="editedPromotion.minSpend" class="form-control form-control-sm" /></td>
                <!-- 優惠碼 -->
                <td><input v-model="editedPromotion.code" class="form-control form-control-sm" /></td>
                <!-- 使用上限 -->
                <td><input v-model.number="editedPromotion.maxUsage" class="form-control form-control-sm" /></td>
                <!-- 每人上限 -->
                <td><input v-model.number="editedPromotion.userUsageLimit" class="form-control form-control-sm" /></td>
                <!-- 餐廳條件 -->
                <td>
                <label><input type="checkbox" v-model="editedPromotion.bindRestaurant" /> 餐廳</label>
                <input v-if="editedPromotion.bindRestaurant" v-model="editedPromotion.restaurantIdsInput" placeholder="ID" class="form-control form-control-sm mt-1" />
                </td>
                <!-- 食物條件 -->
                <td>
                <label><input type="checkbox" v-model="editedPromotion.bindFood" /> 食物</label>
                <input v-if="editedPromotion.bindFood" v-model="editedPromotion.foodCategoryIdsInput" placeholder="ID" class="form-control form-control-sm mt-1" />
                </td>
                <!-- 會員條件 -->
                <td>
                <label><input type="checkbox" v-model="editedPromotion.bindPlan" /> 會員</label>
                <input v-if="editedPromotion.bindPlan" v-model="editedPromotion.planIdsInput" placeholder="ID" class="form-control form-control-sm mt-1" />
                </td>
                <td>{{ promotion.storeName || '無' }}</td>
                <td>{{ promotion.tagName || '無' }}</td>
                <td>{{ promotion.planName || '無' }}</td>
                <td>
                <button class="btn btn-sm btn-success me-1" @click="savePromotion">✅ 儲存</button>
                <button class="btn btn-sm btn-secondary" @click="cancelEdit">取消</button>
                </td>
            </template>

            <!-- 沒在編輯：正常顯示 -->
            <template v-else>
                <td>{{ promotion.title }}</td>
                <td>{{ promotion.description }}</td>
                <td>{{ promotion.startTime }} ~ {{ promotion.endTime }}</td>
                <td>{{ promotion.discountType }}</td>
                <td>{{ promotion.minSpend }}</td>
                <td>{{ promotion.code }}</td>
                <td>{{ promotion.maxUsage }}</td>
                <td>{{ promotion.userUsageLimit }}</td>
                <td>{{ promotion.storeName || '無' }}</td>
                <td>{{ promotion.tagName || '無' }}</td>
                <td>{{ promotion.planName || '無' }}</td>
                <td>
                <button class="btn btn-sm btn-edit me-1" @click="editPromotion(promotion)">修改</button>
                <button class="btn btn-sm btn-delete" @click="deletePromotion(promotion.id)">刪除</button>
                </td>
            </template>
            </tr>

            <tr>
                <td><input v-model="newPromotion.title" placeholder="標題" class="form-control form-control-sm" /></td>
                <td><input v-model="newPromotion.description" placeholder="內容" class="form-control form-control-sm" /></td>
                <td>
                <input type="datetime-local" v-model="newPromotion.startTime" class="form-control form-control-sm mb-1" />
                <input type="datetime-local" v-model="newPromotion.endTime" class="form-control form-control-sm" />
                </td>
                <td><input v-model="newPromotion.discountType" placeholder="類型" class="form-control form-control-sm" /></td>
                <td><input type="number" v-model="newPromotion.minSpend" placeholder="門檻" class="form-control form-control-sm" /></td>
                <td><input v-model="newPromotion.code" placeholder="優惠碼" class="form-control form-control-sm" /></td>
                <td><input type="number" v-model="newPromotion.maxUsage" placeholder="上限" class="form-control form-control-sm" /></td>
                <td><input type="number" v-model="newPromotion.userUsageLimit" placeholder="每人上限" class="form-control form-control-sm" /></td>
                <td>
                <label><input type="checkbox" v-model="newPromotion.bindRestaurant" /> 餐廳</label>
                <input v-if="newPromotion.bindRestaurant" v-model="newPromotion.restaurantIdsInput" placeholder="ID" class="form-control form-control-sm mt-1" />
                </td>
                <td>
                <label><input type="checkbox" v-model="newPromotion.bindFood" /> 食物</label>
                <input v-if="newPromotion.bindFood" v-model="newPromotion.foodCategoryIdsInput" placeholder="ID" class="form-control form-control-sm mt-1" />
                </td>
                <td>
                <label><input type="checkbox" v-model="newPromotion.bindPlan" /> 會員</label>
                <input v-if="newPromotion.bindPlan" v-model="newPromotion.planIdsInput" placeholder="ID" class="form-control form-control-sm mt-1" />
                </td>
                <td>—</td>
            </tr>
            </tbody>
        </table>
        <div class="action-buttons mb-4">
            <button class="btn btn-add" @click="createPromotion">➕ 新增</button>
            <button class="btn btn-cancel" @click="resetNewPromotion">✖️ 取消</button>
        </div>
        </div>

        <div class="pagination d-flex justify-content-end align-items-center pagebar-wrap">
        <button class="btn btn-outline-secondary me-2" :disabled="currentPage === 1" @click="currentPage--">&lt; 上一頁</button>
        <nav>
            <ul class="pagination mb-0">
            <li class="page-item disabled">
                <span class="page-link">頁數：{{ currentPage }} / {{ totalPages }}</span>
            </li>
            </ul>
        </nav>
        <button class="btn btn-outline-secondary ms-2" :disabled="currentPage === totalPages" @click="currentPage++">下一頁 &gt;</button>
        <div class="ms-3">
            <select class="form-select" v-model.number="itemsPerPage" style="width:120px; min-width: 90px;">
            <option v-for="s in [5, 10, 20]" :key="s" :value="s">{{ s }}/每頁</option>
            </select>
        </div>
        </div>
    </main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '@/plungins/axios.js'

const promotions = ref([])
const sidebarOpen = ref(false)
const currentPage = ref(1)
const itemsPerPage = ref(5)
const keyword = ref('')
const selectedType = ref('')

// ➕ 新增用的欄位
const newPromotion = ref({
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    discountType: '',
    minSpend: 0,
    code: '',
    maxUsage: 0,
    userUsageLimit: 0,
    bindRestaurant: false,
    restaurantIdsInput: '',
    bindFood: false,
    foodCategoryIdsInput: '',
    bindPlan: false,
    planIdsInput: ''
})

const toggleSidebar = () => {
    sidebarOpen.value = !sidebarOpen.value
}

// ✅ 防止 null.toLowerCase() 的錯誤
const filteredPromotions = computed(() => {
    return promotions.value.filter(p =>
        (p.title || '').toLowerCase().includes((keyword.value || '').toLowerCase()) &&
        (selectedType.value ? p.discountType === selectedType.value : true)
    )
})

const paginatedPromotions = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value
    return filteredPromotions.value.slice(start, start + itemsPerPage.value)
})

const totalPages = computed(() =>
    Math.ceil(filteredPromotions.value.length / itemsPerPage.value)
)

// 🚀 載入優惠活動
const fetchPromotions = async () => {
    try {
        const response = await axios.get('/promotions')
        console.log('✅ 後端回傳：', response.data)
        promotions.value = response.data
    } catch (e) {
        console.error('❌ 載入後台優惠券失敗', e)
        alert('無法載入優惠券，請檢查後端連線或資料格式')
    }
}

// ➕ 新增優惠活動
const createPromotion = async () => {
    try {
        const promotion = {
        title: newPromotion.value.title,
        description: newPromotion.value.description,
        discountType: newPromotion.value.discountType,
        discountValue: '0.9',
        minSpend: newPromotion.value.minSpend,
        startTime: newPromotion.value.startTime,
        endTime: newPromotion.value.endTime,
        code: newPromotion.value.code,
        maxUsage: newPromotion.value.maxUsage,
        userUsageLimit: newPromotion.value.userUsageLimit,
        storeId: newPromotion.value.bindRestaurant
            ? parseInt(newPromotion.value.restaurantIdsInput)
            : null,
        tagId: newPromotion.value.bindFood
            ? parseInt(newPromotion.value.foodCategoryIdsInput)
            : null,
        planId: newPromotion.value.bindPlan
            ? parseInt(newPromotion.value.planIdsInput)
            : null
        }

        console.log('🚀 即將送出新增資料', promotion)

        const response = await axios.post('/promotions', promotion)
        console.log('✅ 新增成功', response.data)
        alert('✅ 優惠活動新增成功！')
        await fetchPromotions()
        resetNewPromotion()
    } catch (error) {
        console.error('❌ 新增失敗', error)
        alert('新增優惠券失敗，請確認欄位格式')
    }
}


// 🔄 重設新增欄位
const resetNewPromotion = () => {
    newPromotion.value = {
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        discountType: '',
        minSpend: 0,
        code: '',
        maxUsage: 0,
        userUsageLimit: 0,
        bindRestaurant: false,
        restaurantIdsInput: '',
        bindFood: false,
        foodCategoryIdsInput: '',
        bindPlan: false,
        planIdsInput: ''
    }
}

// ❌ 刪除
const deletePromotion = async (id) => {
    if (confirm('你確定要刪除這筆優惠券嗎？')) {
        try {
        await axios.delete(`/promotions/${id}`)
        alert('✅ 優惠券已刪除')
        await fetchPromotions()
        } catch (error) {
        console.error('❌ 刪除失敗', error)
        alert('刪除失敗，請稍後再試')
        }
    }
}

// 📝 編輯
const editingId = ref(null) // 用來記錄目前正在編輯哪一筆
const editedPromotion = ref({}) // 暫存編輯內容

const editPromotion = (promotion) => {
    editingId.value = promotion.id
    editedPromotion.value = { ...promotion,
    bindRestaurant: !!promotion.storeName,
    restaurantIdsInput: promotion.storeId || '',
    bindFood: !!promotion.tagName,
    foodCategoryIdsInput: promotion.tagId || '',
    bindPlan: !!promotion.planName,
    planIdsInput: promotion.planId || '' 
    }
}

const cancelEdit = () => {
    editingId.value = null
    editedPromotion.value = {}
}

const savePromotion = async () => {
    try {
        const updated = {
        ...editedPromotion.value,
        storeId: editedPromotion.value.bindRestaurant ? parseInt(editedPromotion.value.restaurantIdsInput) : null,
        tagId: editedPromotion.value.bindFood ? parseInt(editedPromotion.value.foodCategoryIdsInput) : null,
        planId: editedPromotion.value.bindPlan ? parseInt(editedPromotion.value.planIdsInput) : null
        }

        const response = await axios.put(`/promotions/${editingId.value}`, updated, {
        headers: {
            'Content-Type': 'application/json'
        }
        })

        console.log('✅ 更新成功', response.data)
        alert('✅ 更新成功')
        await fetchPromotions()
        cancelEdit()
    } catch (error) {
        console.error('❌ 更新失敗', error)
        alert('更新失敗，請檢查欄位格式或再試一次')
    }
}




// 🔍 清除篩選條件
const resetFilters = () => {
    keyword.value = ''
    selectedType.value = ''
}

const prevPage = () => {
    if (currentPage.value > 1) currentPage.value--
}
const nextPage = () => {
    if (currentPage.value < totalPages.value) currentPage.value++
}

onMounted(fetchPromotions)
</script>

<style>
/* 若已載入 admin-style.css，這裡可省略 */
</style>
