<template>
    <div class="page-wrapper">
        <!-- Header: 在 Flex 容器中，它是一個獨立的區塊 -->
        <header class="bg-warning p-3 d-flex justify-content-between align-items-center shadow-sm flex-shrink-0">
            <!-- 左側的 Logo 和標題 -->
            <div class="d-flex align-items-center">
                <!-- 使用 <img> 標籤，並將 src 動態綁定到我們導入的 logoUrl 變數 -->
                <img :src="logoUrl" alt="Logo" style="height: 70px; margin-right: 12px;">
                <h1 class="h5 m-0">商家管理中心</h1>
            </div>
            <div>
                <span>Kinan, 您好！</span>
                <img :src="avataUrl" alt="Avata" style="height: 70px; margin-right: 12px;">
                <!-- <img src="https://via.placeholder.com/40" class="rounded-circle ms-2" alt="Avatar"> -->
            </div>
        </header>

        <div class="main-container">
            <!-- Sidebar: 現在是 main-container 的一個 flex item -->
            <nav class="sidebar bg-light p-3">
                <!-- ... 側邊欄內容不變 ... -->
                <div class="sidebar-sticky">
                    <h6 class="sidebar-heading px-3 mt-4 mb-1 text-muted">管理你的商家資訊</h6>
                    <ul class="nav flex-column">
                        <li class="nav-item"><a class="nav-link" href="#">👨‍🍳商家資料</a></li>
                        <li class="nav-item"><a class="nav-link active" href="#">🍳菜單管理</a></li>
                        <li class="nav-item"><a class="nav-link" href="#">🍽️店鋪管理</a></li>
                    </ul>
                    <h6 class="sidebar-heading px-3 mt-4 mb-1 text-muted">主要功能設定</h6>
                    <ul class="nav flex-column mb-2">
                        <li class="nav-item"><a class="nav-link" href="#">📃訂單管理</a></li>
                        <li class="nav-item"><a class="nav-link" href="#">🪑訂位管理</a></li>
                        <li class="nav-item"><a class="nav-link" href="#">🕒營業時間</a></li>
                        <li class="nav-item"><a class="nav-link" href="#">📰評論回覆</a></li>
                    </ul>
                </div>
            </nav>

            <!-- Main Content: 也是 main-container 的一個 flex item -->
            <main class="main-content p-4">
                <div
                    class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
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
                        <a class="nav-link" :class="{ active: activeTab === 'overview' }" href="#"
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
                    @add-new-item="openItemModal(null)"
                    @edit-item="openItemModal" />
                    
                    <CustomizationSpecs v-if="activeTab === 'specs'"
                    :specs="specs"
                    @add-new-spec="() => alert('準備新增規格！')"
                    @edit-spec="(spec) => alert(`準備編輯規格: ${spec.name}`)"
                    />
                </div>
            </main>
        </div>

        <!-- Footer -->
        <footer class="bg-warning text-white text-center p-3 flex-shrink-0">
            Footer
        </footer>


        <!-- Modals (不受佈局影響) -->
        <EditItemModal v-if="isItemModalOpen" :item="currentEditingItem" @close="closeItemModal" @save="handleSaveItem"
            @delete="handleDeleteItem" />
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import MenuOverview from './MenuOverview.vue';
import CustomizationSpecs from './CustomizationSpecs.vue';
import EditItemModal from '../components/EditItemModal.vue';
// import EditSpecModal from '../components/EditSpecModal.vue';
import logoUrl from '../assets/logo.png'; // 引入 logo 圖片
import avataUrl from '../assets/avata.png'; // 引入 avata 圖片

// --- 響應式狀態 (State) ---

// 當前活動的 Tab
const activeTab = ref('overview'); // 'overview' 或 'specs'

// 模擬的商店資料
const stores = ref([
    { id: 'd-aan-store', name: '呷蝦密(大安門市)' },
    { id: 'gong-guan-store', name: '呷蝦密(公館門市)' },
    { id: 'gu-ting-store', name: '呷蝦密(古亭門市)' },
]);
const selectedStore = ref('d-aan-store');

// --- 菜單品項相關狀態 ---

// 模擬的菜單品項資料
const items = reactive([
    { id: 1, name: '經典拿鐵', price: 70, status: '供應中', stock: 50, img: 'https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 2, name: '西西里咖啡', price: 85, status: '供應中', stock: 30, img: 'https://images.pexels.com/photos/1449386/pexels-photo-1449386.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 3, name: '黑糖拿鐵', price: 70, status: '暫停供應', stock: 0, img: 'https://images.pexels.com/photos/3806690/pexels-photo-3806690.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 4, name: '煙燻鮭魚帕尼尼', price: 150, status: '供應中', stock: 20, img: 'https://images.pexels.com/photos/1399920/pexels-photo-1399920.jpeg?auto=compress&cs=tinysrgb&w=600' },
    {
        id: 5,
        name: '草莓蛋糕',
        price: 120,
        status: '供應中',
        stock: 15,
        // 蛋糕圖片
        img: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?q=80&w=600'
    },
]);

// 控制編輯品項 Modal 的開關
const isItemModalOpen = ref(false);
// 正在編輯的品項，null 代表是新增
const currentEditingItem = ref(null);

// --- 客製化規格相關狀態 ---

// 模擬的客製化規格資料
const specs = reactive([
    { id: 'spec1', name: '附餐選擇', options: '六塊雞、中薯、洋蔥圈...', status: '供應中' },
    { id: 'spec2', name: '冰熱選擇', options: '正常冰、少冰、去冰...', status: '供應中' },
    { id: 'spec3', name: '甜度選擇', options: '全糖、七分糖、五分糖...', status: '暫停供應' },
]);

// 控制編輯規格 Modal 的開關
const isSpecModalOpen = ref(false);
// 正在編輯的規格，null 代表是新增
const currentEditingSpec = ref(null);


// --- 方法 (Methods) ---

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
// 3. 為未來的 EditSpecModal 準備方法
// const openSpecModal = (spec) => {
//   console.log('準備打開規格 Modal，編輯的資料是:', spec);
//   // currentEditingSpec.value = spec ? { ...spec } : null;
//   // isSpecModalOpen.value = true;
// };

// --- 規格 Modal 相關方法 (此處為簡化版，您可以比照品項邏輯擴充) ---
const openSpecModal = (spec) => {
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

</script>

<style scoped>
.page-wrapper {
    display: flex;
    flex-direction: column;
    /* 讓 header, main-container, footer 垂直排列 */
    height: 100vh;
}

.main-container {
    display: flex;
    /* 讓 sidebar 和 main-content 水平排列 */
    flex-grow: 1;
    /* 佔滿 page-wrapper 中除了 header 和 footer 的所有空間 */
    overflow: hidden;
}

.sidebar {
    flex-shrink: 0;
    width: 250px;
    overflow-y: auto;
    border-right: 1px solid #dee2e6;
    /* 模仿原始設計的陰影/邊框 */
}

.main-content {
    flex-grow: 1;
    overflow-y: auto;
}

.nav-link.active {
    font-weight: bold;
    color: #0d6efd;
}

/* 確保 header 和 footer 不會被壓縮 */
header,
footer {
    flex-shrink: 0;
}
</style>