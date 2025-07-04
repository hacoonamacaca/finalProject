<script setup>
import { ref, reactive, onMounted } from 'vue'; // 導入 onMounted 函數
import { apiService } from '../services/apiService.js'; // 導入 API 服務
import MenuOverview from '../components/menu/MenuOverview.vue';
import CustomizationSpecs from '../components/menu/CustomizationSpecs.vue';
import EditItemModal from '../components/menu/EditItemModal.vue';
import EditSpecModal from '../components/menu/EditSpecModal.vue';

// --- 響應式狀態 (State) ---

// =================================================================
// 1. 組件核心狀態 (Component Core State)
// =================================================================

// 當前活動的 Tab
const activeTab = ref('overview'); // 'overview' 或 'specs'

// 模擬的商店資料
const stores = ref([
    { id: 'd-aan-store', name: '呷蝦密(大安門市)' },
    { id: 'gong-guan-store', name: '呷蝦密(公館門市)' },
    { id: 'gu-ting-store', name: '呷蝦密(古亭門市)' },
]);
const selectedStore = ref('d-aan-store');

// =================================================================
// 2. 資料源 (Data Sources) - 模擬從後端獲取的資料
// =================================================================

// 新增 categories 陣列
const categories = reactive([
    // 模擬 categories 陣列
    // { id: 'cat-1', name: '招牌飲品', order: 1 },
    // { id: 'cat-2', name: '義式咖啡', order: 2 },
    // { id: 'cat-3', name: '炭烤三明治', order: 3 },
    // { id: 'cat-4', name: '帕尼尼', order: 4 },
]);


const items = reactive([
    // 模擬的菜單品項資料
    // { id: 1, 
    //   name: '經典拿鐵', 
    //   price: 70, 
    //   status: '供應中', 
    //   stock: 50, 
    //   img: 'https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg?auto=compress&cs=tinysrgb&w=600', 
    //   categoryId: 'cat-1' },
]);


const specs = reactive([
    // 模擬的客製化規格資料
    // {
    //     id: 'spec1',
    //     name: '附餐選擇',
    //     minSelection: 1, // 加上 min/max
    //     maxSelection: 1,
    //     // 將 options 改為物件陣列
    //     options: [
    //         { id: 'opt-a1', name: '六塊雞', price: 20, status: '供應中' },
    //         { id: 'opt-a2', name: '中薯', price: 10, status: '供應中' },
    //         { id: 'opt-a3', name: '洋蔥圈', price: 15, status: '供應中' },
    //         { id: 'opt-a4', name: '蘋果派', price: 15, status: '暫停供應' },
    //     ]
    // },
]);

const isLoading = ref(true); // 新增一個加載狀態，用於顯示讀取中的提示

const error = ref(null); // 新增一個錯誤狀態

// =================================================================
// 3. 在組件掛載時獲取所有資料 (onMounted)
// =================================================================
onMounted(async () => {
    try {
        isLoading.value = true;
        error.value = null;

        // 使用 Promise.all 來並行發送所有請求，效率更高
        const [categoriesData, itemsData, specsData] = await Promise.all([
            apiService.getCategories(),
            apiService.getItems(),
            apiService.getSpecs(),
        ]);

        // 將獲取到的資料賦值給我們的響應式變數
        // 使用 Object.assign 或 .splice(0) 來更新 reactive 陣列
        Object.assign(categories, categoriesData);
        Object.assign(items, itemsData);
        Object.assign(specs, specsData);

    } catch (e) {
        console.error('Failed to fetch initial data:', e);
        error.value = '無法載入資料，請稍後再試。';
    } finally {
        isLoading.value = false; // 無論成功或失敗，都結束加載狀態
    }
});

// =================================================================
// 4. 品項管理相關 (Item Management)
// =================================================================

const isItemModalOpen = ref(false); // 控制編輯品項 Modal 的開關

const currentEditingItem = ref(null); // 正在編輯的品項，null 代表是新增

// =================================================================
// 5. 規格管理相關 (Specification Management)
// =================================================================

// 控制編輯規格 Modal 的開關
const isSpecModalOpen = ref(false);

// 正在編輯的規格，null 代表是新增
const currentEditingSpec = ref(null);

const openSpecModal = (spec) => {
    console.log('打開規格 Modal，編輯的資料是:', spec);
    currentEditingSpec.value = spec ? { ...spec } : null;
    isSpecModalOpen.value = true;
};

const closeSpecModal = () => {
    isSpecModalOpen.value = false;
}

const handleSaveSpec = (specData) => {
    console.log('儲存規格:', specData);
    alert('規格已儲存！');
    closeSpecModal();
}

const handleDeleteSpec = (specId) => {
    if (confirm('確定要刪除此規格嗎？')) {
        alert('規格已刪除！');
        closeSpecModal();
    }
}

// =================================================================
// 6. 通用方法 (General Methods)
// =================================================================

// 切換 Tab
const selectTab = (tab) => {
    activeTab.value = tab;
};

// --- 品項 Modal 相關方法 ---
const openItemModal = (item) => {
    // 如果傳入了 item，就是編輯；否則就是新增
    currentEditingItem.value = item ? { ...item } : null; // 傳遞複本以避免雙向綁定問題
    isItemModalOpen.value = true;
};

const closeItemModal = () => {
    isItemModalOpen.value = false;
};

const handleSaveItem = (itemData) => {
    console.log('從 Modal 接收到儲存的資料:', itemData);
    if (itemData.id) {
        // 編輯：找到對應 id 並更新
        const index = items.findIndex(i => i.id === itemData.id);
        if (index !== -1) {
            items[index] = itemData;
        }
    } else {
        // 新增：給一個新的 id 並推進陣列
        const newItem = { ...itemData, id: Date.now() }; // 用時間戳當臨時 id
        items.push(newItem);
    }
    // 模擬 API 儲存後的流程
    alert('儲存中...');
    setTimeout(() => {
        alert('儲存成功！');
        closeItemModal();
    }, 1000);
};

const handleDeleteItem = (itemId) => {
    const index = items.findIndex(i => i.id === itemId);
    if (index !== -1) {
        if (confirm('確定要刪除此品項嗎？')) {
            items.splice(index, 1);
            alert('刪除成功！');
            closeItemModal();
        }
    }
}
</script>

<template>
    <div>
        <div
            class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
            <div class="d-flex align-items-center">
                <h2 class="h3 mb-0">菜單管理</h2> <!-- mb-0 移除 h3 預設的下邊距 -->                
                <select class="form-select ms-3" v-model="selectedStore" style="width: auto; min-width: 180px;"> <!-- 切換店鋪下拉選單 -->
                    <option v-for="store in stores" :key="store.id" :value="store.id">
                        {{ store.name }}
                    </option>
                </select>                
            </div>
            <div>
            <!-- 未來的功能按鈕可以放這裡 -->
            </div>
        </div>

        <!-- 在列表區域的外面，加上 loading 和 error 的判斷 -->
        <div v-if="isLoading" class="text-center p-5">
            <div class="spinner-border" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">資料載入中...</p>
        </div>

        <div v-else-if="error" class="alert alert-danger">
            {{ error }}
        </div>

        <div v-else>
            <ul class="nav nav-tabs">
                <li class="nav-item">
                    <a class="nav-link" :class="{ active: activeTab === 'overview' }" href="#"
                        @click.prevent="selectTab('overview')">菜單總覽</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" :class="{ active: activeTab === 'specs' }" href="#"
                        @click.prevent="selectTab('specs')">客製化規格</a>
                </li>
            </ul>

            <div class="mt-4">
                <MenuOverview v-if="activeTab === 'overview'" :items="items" :categories="categories"
                    @add-new-item="openItemModal(null)" @edit-item="openItemModal" />

                <CustomizationSpecs v-if="activeTab === 'specs'" :specs="specs" @add-new-spec="openSpecModal(null)"
                    @edit-spec="openSpecModal" />
            </div>
        </div>

        <!-- Modals (不受佈局影響) -->
        <EditItemModal v-if="isItemModalOpen" :item="currentEditingItem" :categories="categories"
            @close="closeItemModal" @save="handleSaveItem" @delete="handleDeleteItem" />

        <EditSpecModal v-if="isSpecModalOpen" :spec="currentEditingSpec" @close="closeSpecModal" @save="handleSaveSpec"
            @delete="handleDeleteSpec" />
    </div>
</template>

<style scoped>
/* 已搬到共用樣式 SellerLayout.vue */
</style>