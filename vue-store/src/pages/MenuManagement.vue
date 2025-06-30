<template>
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h2 class="h3">菜單管理</h2>
        <div class="btn-toolbar mb-2 mb-md-0">
            <select class="form-select" v-model="selectedStore">
                <option v-for="store in stores" :key="store.id" :value="store.id">
                    {{ store.name }}
                </option>
            </select>
        </div>
    </div>
        <ul class="nav nav-tabs">
            <li class="nav-item">
                <a class="nav-link" 
                :class="{ active: activeTab === 'overview' }" href="#"
                @click.prevent="selectTab('overview')">菜單總覽</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" :class="{ active: activeTab === 'specs' }" href="#"
                @click.prevent="selectTab('specs')">客製化規格</a>
            </li>
        </ul>
        <div class="mt-4">
            <MenuOverview v-if="activeTab === 'overview'" 
                :items="items"
                :categories="categories" 
                @add-new-item="openItemModal(null)"
                @edit-item="openItemModal" 
                />
                    
            <CustomizationSpecs v-if="activeTab === 'specs'"
                :specs="specs"
                @add-new-spec="() => alert('準備新增規格！')"
                @edit-spec="(spec) => alert(`準備編輯規格: ${spec.name}`)"
                />
        </div>
        
        <!-- Modals (不受佈局影響) -->
        <EditItemModal v-if="isItemModalOpen" 
        :item="currentEditingItem" 
        :categories="categories"
        @close="closeItemModal" 
        @save="handleSaveItem"
        @delete="handleDeleteItem" 
        />

</template>

<script setup>
import { ref, reactive } from 'vue';
import MenuOverview from './MenuOverview.vue';
import CustomizationSpecs from './CustomizationSpecs.vue';
import EditItemModal from '../components/EditItemModal.vue';
// import EditSpecModal from '../components/EditSpecModal.vue';

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
    { id: 'cat-1', name: '招牌飲品', order: 1 },
    { id: 'cat-2', name: '義式咖啡', order: 2 },
    { id: 'cat-3', name: '炭烤三明治', order: 3 },
    { id: 'cat-4', name: '帕尼尼', order: 4 },
]);

// 模擬的菜單品項資料
const items = reactive([
    { id: 1, name: '經典拿鐵', price: 70, status: '供應中', stock: 50, img: 'https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg?auto=compress&cs=tinysrgb&w=600', categoryId: 'cat-1' },
    { id: 2, name: '西西里咖啡', price: 85, status: '供應中', stock: 30, img: 'https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg?auto=compress&cs=tinysrgb&w=600', categoryId: 'cat-1' },
    { id: 3, name: '經典義式咖啡', price: 60, status: '供應中', stock: 100, img: 'https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg?auto=compress&cs=tinysrgb&w=600', categoryId: 'cat-2' },
    { id: 4, name: '煙燻雞肉三明治', price: 90, status: '供應中', stock: 20, img: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?q=80&w=600', categoryId: 'cat-3' },
    { id: 5, name: '花生醬培根三明治', price: 95, status: '暫停供應', stock: 0, img: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?q=80&w=600', categoryId: 'cat-3' },
    { id: 6, name: '費城牛肉帕尼尼', price: 150, status: '供應中', stock: 15, img: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?q=80&w=600', categoryId: 'cat-4' },
]);

// 模擬的客製化規格資料
const specs = reactive([
    { id: 'spec1', name: '附餐選擇', options: '六塊雞、中薯、洋蔥圈...', status: '供應中' },
    { id: 'spec2', name: '冰熱選擇', options: '正常冰、少冰、去冰...', status: '供應中' },
    { id: 'spec3', name: '甜度選擇', options: '全糖、七分糖、五分糖...', status: '暫停供應' },
]);

// =================================================================
// 3. 品項管理相關 (Item Management)
// =================================================================

// 控制編輯品項 Modal 的開關
const isItemModalOpen = ref(false);

// 正在編輯的品項，null 代表是新增
const currentEditingItem = ref(null);

// 控制編輯規格 Modal 的開關
const isSpecModalOpen = ref(false);

// 正在編輯的規格，null 代表是新增
const currentEditingSpec = ref(null);

// =================================================================
// 4. 規格管理相關 (Specification Management)
// =================================================================
// 為未來的 EditSpecModal 準備方法

// openSpecModal = (spec) => {
// console.log('準備打開規格 Modal，編輯的資料是:', spec);
// currentEditingSpec.value = spec ? { ...spec } : null;
// isSpecModalOpen.value = true;
// };

// const openSpecModal = (spec) => {
//     currentEditingSpec.value = spec ? { ...spec } : null;
//     isSpecModalOpen.value = true;
// };

// const closeSpecModal = () => {
//     isSpecModalOpen.value = false;
// }

// const handleSaveSpec = (specData) => {
//     console.log('儲存規格:', specData);
//     alert('規格已儲存！');
//     closeSpecModal();
// }

// const handleDeleteSpec = (specId) => {
//     if (confirm('確定要刪除此規格嗎？')) {
//         alert('規格已刪除！');
//         closeSpecModal();
//     }
// }

// =================================================================
// 5. 通用方法 (General Methods)
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

<style scoped>
/* 已搬到共用樣式 SellerLayout.vue */
</style>