<template>
    <div class="admin-layout">
        <!-- Header -->
        <header class="admin-header">
            <h1>網站後台管理系統</h1>
        </header>

        <!-- Sidebar + Main -->
        <div class="admin-body">
            <aside class="admin-sidebar">
                <nav>
                    <ul>
                        <li>優惠活動管理</li>
                        <li>訂單管理</li>
                        <li>用戶管理</li>
                    </ul>
                </nav>
            </aside>

            <main class="admin-main">
                <h2>篩選</h2>

                <!-- 篩選 -->
                <div class="filter-bar">
                    <input v-model="keyword" placeholder="搜尋活動標題..." />
                    <select v-model="selectedType">
                        <option value="">全部折扣類型</option>
                        <option value="percentage">百分比折扣</option>
                        <option value="fixed">固定金額</option>
                    </select>
                    <button @click="resetFilters">清除篩選</button>
                </div>

                <!-- 表格卡片 -->
                <div class="table-card">
                    <table class="promotion-table">
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
                            <tr v-for="promotion in paginatedPromotions" :key="promotion.promotion_id">
                                <td>{{ promotion.title }}</td>
                                <td>{{ promotion.description }}</td>
                                <td>{{ promotion.start_time }} ~ {{ promotion.end_time }}</td>
                                <td>{{ promotion.discount_type }}</td>
                                <td>{{ promotion.min_spend }}</td>
                                <td>{{ promotion.code }}</td>
                                <td>{{ promotion.max_usage }}</td>
                                <td>{{ promotion.user_usage_limit }}</td>
                                <td>
                                    {{ promotion.restaurant_ids.length > 0 ? promotion.restaurant_ids.join(', ') : '無'
                                    }}
                                </td>
                                <td>
                                    {{ promotion.food_category_ids.length > 0 ? promotion.food_category_ids.join(', ') :
                                    '無' }}
                                </td>
                                <td>
                                    {{ promotion.plan_ids.length > 0 ? promotion.plan_ids.join(', ') : '無' }}
                                </td>
                                <td>
                                    <button class="btn-edit">修改</button>
                                    <button class="btn-delete"
                                        @click="deletePromotion(promotion.promotion_id)">刪除</button>
                                </td>
                            </tr>

                            <!-- 新增列 -->
                            <tr>
                                <td><input v-model="newPromotion.title" placeholder="標題" /></td>
                                <td><input v-model="newPromotion.description" placeholder="內容" /></td>
                                <td>
                                    <input type="datetime-local" v-model="newPromotion.start_time" /> ~
                                    <input type="datetime-local" v-model="newPromotion.end_time" />
                                </td>
                                <td><input v-model="newPromotion.discount_type" placeholder="類型" /></td>
                                <td><input type="number" v-model="newPromotion.min_spend" placeholder="門檻" /></td>
                                <td><input v-model="newPromotion.code" placeholder="優惠碼" /></td>
                                <td><input type="number" v-model="newPromotion.max_usage" placeholder="上限" /></td>
                                <td><input type="number" v-model="newPromotion.user_usage_limit" placeholder="每人上限" />
                                </td>

                                <!-- 綁定條件 -->
                                <td>
                                    <label>
                                        <input type="checkbox" v-model="newPromotion.bindRestaurant" /> 餐廳標籤
                                    </label>
                                    <input v-if="newPromotion.bindRestaurant"
                                        v-model="newPromotion.restaurant_ids_input" placeholder="輸入ID, 逗號分隔" />
                                </td>
                                <td>
                                    <label>
                                        <input type="checkbox" v-model="newPromotion.bindFood" /> 食物標籤
                                    </label>
                                    <input v-if="newPromotion.bindFood" v-model="newPromotion.food_category_ids_input"
                                        placeholder="輸入ID, 逗號分隔" />
                                </td>
                                <td>
                                    <label>
                                        <input type="checkbox" v-model="newPromotion.bindPlan" /> 會員標籤
                                    </label>
                                    <input v-if="newPromotion.bindPlan" v-model="newPromotion.plan_ids_input"
                                        placeholder="輸入ID, 逗號分隔" />
                                </td>

                                <td>—</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- 新增 / 取消 -->
                <div class="action-buttons">
                    <button class="btn-add" @click="createPromotion">新增</button>
                    <button class="btn-cancel" @click="resetNewPromotion">取消</button>
                </div>

                <!-- 分頁 -->
                <div class="pagination">
                    <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
                    <span>頁數：{{ currentPage }} / {{ totalPages }}</span>
                    <button @click="nextPage" :disabled="currentPage === totalPages">下一頁</button>
                </div>
            </main>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// promotions
const promotions = ref([])
const currentPage = ref(1)
const itemsPerPage = ref(5)

const keyword = ref('')
const selectedType = ref('')

// 新增 Promotion 物件
const newPromotion = ref({
    title: '',
    description: '',
    start_time: '',
    end_time: '',
    discount_type: '',
    min_spend: 0,
    code: '',
    max_usage: 0,
    user_usage_limit: 0,
    bindRestaurant: false,
    restaurant_ids_input: '',
    bindFood: false,
    food_category_ids_input: '',
    bindPlan: false,
    plan_ids_input: '',
})

// 篩選 & 分頁
const filteredPromotions = computed(() => {
    return promotions.value.filter(p => {
        const matchKeyword = p.title.toLowerCase().includes(keyword.value.toLowerCase())
        const matchType = selectedType.value ? p.discount_type === selectedType.value : true
        return matchKeyword && matchType
    })
})

const paginatedPromotions = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value
    return filteredPromotions.value.slice(start, start + itemsPerPage.value)
})

const totalPages = computed(() => Math.ceil(filteredPromotions.value.length / itemsPerPage.value))

// 假資料
const fetchPromotions = () => {
    promotions.value = [
        {
            promotion_id: 1,
            title: '週年慶9折',
            description: '限時全店9折',
            start_time: '2025-06-01',
            end_time: '2025-06-30',
            discount_type: 'percentage',
            min_spend: 0,
            code: 'ANNI2025',
            max_usage: 1000,
            user_usage_limit: 1,
            restaurant_ids: ['1'],
            food_category_ids: [],
            plan_ids: [],
        },
        {
            promotion_id: 2,
            title: '拉麵活動',
            description: '滿500折100',
            start_time: '2025-06-12',
            end_time: '2025-06-18',
            discount_type: 'fixed',
            min_spend: 500,
            code: 'RAMEN100',
            max_usage: 200,
            user_usage_limit: 1,
            restaurant_ids: [],
            food_category_ids: ['2'],
            plan_ids: [],
        },
        {
            promotion_id: 3,
            title: '銀會員禮',
            description: '每月送 2 張 60 折價券',
            start_time: '2025-06-01',
            end_time: '2025-06-30',
            discount_type: 'fixed',
            min_spend: 0,
            code: 'SILVER60',
            max_usage: null,
            user_usage_limit: 2,
            restaurant_ids: [],
            food_category_ids: [],
            plan_ids: ['2'],
        },
    ]
}

const createPromotion = () => {
    const newId = promotions.value.length
        ? promotions.value[promotions.value.length - 1].promotion_id + 1
        : 1

    const promotion = {
        promotion_id: newId,
        ...newPromotion.value,
        restaurant_ids: newPromotion.value.bindRestaurant
            ? newPromotion.value.restaurant_ids_input.split(',').map(id => id.trim()).filter(id => id)
            : [],
        food_category_ids: newPromotion.value.bindFood
            ? newPromotion.value.food_category_ids_input.split(',').map(id => id.trim()).filter(id => id)
            : [],
        plan_ids: newPromotion.value.bindPlan
            ? newPromotion.value.plan_ids_input.split(',').map(id => id.trim()).filter(id => id)
            : [],
    }

    promotions.value.push(promotion)
    resetNewPromotion()
}

const resetNewPromotion = () => {
    newPromotion.value = {
        title: '',
        description: '',
        start_time: '',
        end_time: '',
        discount_type: '',
        min_spend: 0,
        code: '',
        max_usage: 0,
        user_usage_limit: 0,
        bindRestaurant: false,
        restaurant_ids_input: '',
        bindFood: false,
        food_category_ids_input: '',
        bindPlan: false,
        plan_ids_input: '',
    }
}

const deletePromotion = (id) => {
    promotions.value = promotions.value.filter(p => p.promotion_id !== id)
}

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


<style scoped>
/* 你的原本 CSS 直接沿用不變 */
html,
body {
    margin: 0;
    padding: 0;
    height: 100%;
    background: #f0f2f5;
}

.admin-layout {
    display: flex;
    flex-direction: column;
    height: 100vh;
}

.admin-header {
    background: #094e93;
    color: #fff;
}

.admin-sidebar {
    background: #97b1cb;
    color: #fff;
}

.admin-header {
    padding: 15px 30px;
    font-size: 22px;
    font-weight: bold;
}

.admin-body {
    display: flex;
    flex: 1;
}

.admin-sidebar {
    width: 200px;
    padding: 30px 20px;
}

.admin-sidebar ul {
    list-style: none;
    padding: 0;
}

.admin-sidebar li {
    margin-bottom: 20px;
    cursor: pointer;
}

.admin-sidebar li:hover {
    color: #1abc9c;
}

.admin-main {
    flex: 1;
    margin: 0;
    padding: 30px;
    background: #f0f2f5;
    overflow-y: auto;
}

.admin-main h2 {
    margin-bottom: 20px;
    color: #34495e;
}

.table-card {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    padding: 20px;
}

.filter-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
}

.filter-bar input,
.filter-bar select {
    padding: 10px 12px;
    border: 1px solid #bdc3c7;
    border-radius: 6px;
}

.filter-bar button {
    background: #3498db;
    color: #fff;
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

.filter-bar button:hover {
    background: #2980b9;
}

.promotion-table {
    width: 100%;
    border-collapse: collapse;
    table-layout: fixed;
}

.promotion-table th,
.promotion-table td {
    border: 1px solid #ddd;
    padding: 14px 12px;
    text-align: center;
}

.promotion-table th {
    background: #ecf0f1;
}

.promotion-table tbody tr:nth-child(even) {
    background: #f9f9f9;
}

.promotion-table input,
.promotion-table select {
    width: 100%;
    max-width: 100%;
    box-sizing: border-box;
    padding: 6px 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.btn-add {
    background: #27ae60;
    color: #fff;
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

.btn-add:hover {
    background: #219150;
}

.btn-cancel {
    background: #c0392b;
    color: #fff;
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

.btn-cancel:hover {
    background: #a93226;
}

.btn-edit {
    background: #2980b9;
    color: #fff;
    padding: 6px 12px;
    border: none;
    margin-right: 6px;
    border-radius: 4px;
    cursor: pointer;
}

.btn-edit:hover {
    background: #2471a3;
}

.btn-delete {
    background: #e74c3c;
    color: #fff;
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.btn-delete:hover {
    background: #c0392b;
}

.action-buttons {
    margin: 25px 0;
    display: flex;
    gap: 12px;
    justify-content: flex-end;
}

.pagination {
    display: flex;
    gap: 12px;
    align-items: center;
}

.pagination button {
    background: #34495e;
    color: #fff;
    border: none;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
}

.pagination button:disabled {
    background: #bdc3c7;
    cursor: not-allowed;
}
</style>
