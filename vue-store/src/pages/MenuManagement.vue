<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, watch, computed } from 'vue'; // 導入 onMounted、onBeforeUnmount、watch函數
import SlideOutPanel from '../components/common/SlideOutPanel.vue';
import apiClient from '../plungins/axios.js'; // 導入 apiClient
import { uploadImage } from '../plungins/firebase-storage.js' // 導入 firebase
import PageHeader from '../components/common/PageHeader.vue';
// import CustomizationSpecs from '../components/menu/CustomizationSpecs.vue';  //預定捨棄功能
import EditItemPanel from '../components/menu/EditItemPanel.vue';
import CategoryManagement from '../components/menu/CategoryManagement.vue';
import EditCategoryPanel from '../components/menu/EditCategoryPanel.vue'; 
// import EditSpecModal from '../components/menu/EditSpecModal.vue';  //預定捨棄功能
import MenuOverview from '../components/menu/MenuOverview.vue';

// --- 響應式狀態 (State) ---

// =================================================================
// 1. 組件核心狀態 (Component Core State)
// =================================================================

// 當前活動的 Tab
const activeTab = ref('overview'); // 'overview', 'categories' 或 'specs'

// 模擬的商店資料
// const stores = ref([
//     { id: 1, name: '美味小館' },
//     { id: 6, name: '香辣火鍋' },
// ]);
// 預設選中第一個店家的 ID
// const selectedStore = ref(stores.value[0]?.id || null); 

// 🔥 NEW: 從 localStorage 取得登入用戶資料
const currentUser = ref(null)
const stores = ref([])
const selectedStore = ref(null)

// 載入用戶資料和店家列表的函數
const loadUserData = async () => {
    const ownerId = localStorage.getItem('ownerId')
    const ownerFullName = localStorage.getItem('storeFullName')
    const ownerEmail = localStorage.getItem('storeEmail')
    
    if (ownerId) {
        currentUser.value = {
            ownerId,
            ownerFullName,
            ownerEmail
        }
        
        console.log('✅ 載入用戶資料:', currentUser.value)

        // 🔥 NEW: 向後端請求該 owner 的所有 store 資料
        try {
            console.log(`🚀 正在為 owner ID: ${ownerId} 獲取店家列表...`)
            
            // 🔥 修正 API 路徑：使用正確的 endpoint
            const storesResponse = await apiClient.get('/api/stores/profile/all', {
                params: { ownerId: ownerId }
            })
            console.log('✅ 成功獲取店家列表:', storesResponse.data)
            
            // 更新 stores 陣列
            stores.value = storesResponse.data.map(store => ({
                id: store.id,
                name: store.name || store.storeName || `店家${store.id}`
            }))
            
            // 🔥 NEW: 智慧選擇預設店家
            if (stores.value.length > 0) {
                // 優先選擇 localStorage 中記錄的 storeId
                const savedStoreId = localStorage.getItem('storeId')
                const savedStore = stores.value.find(store => String(store.id) === String(savedStoreId))
                
                if (savedStore) {
                    selectedStore.value = savedStore.id
                    console.log('📌 使用 localStorage 中的店家:', savedStore)
                } else {
                    // 如果沒有或找不到，就選第一個
                    selectedStore.value = stores.value[0].id
                    console.log('📌 選擇第一個店家:', stores.value[0])
                }
            }
            
            console.log('🏪 最終店家狀態:', {
                stores: stores.value,
                selectedStore: selectedStore.value
            })
            
        } catch (error) {
            console.error('❌ 獲取店家列表失敗:', error)
            error.value = `無法載入店家資料：${error.response?.data?.message || error.message}`
            // 發生錯誤時清空資料
            stores.value = []
            selectedStore.value = null
        }
    } else {
        console.warn('⚠️ 找不到 ownerId')
        // 清空資料
        currentUser.value = null
        stores.value = []
        selectedStore.value = null
    }
}


// =================================================================
// 2. 資料源 (Data Sources) - 從後端獲取的資料
// =================================================================

const categories = reactive([]);


const items = reactive([]);


const specs = reactive([]);

const isLoading = ref(false); // 新增一個加載狀態，用於顯示讀取中的提示

const error = ref(null); // 新增一個錯誤狀態


// =================================================================
// 3. 【全新】獲取資料的核心函式
// =================================================================

const fetchMenuData = async (storeId) => {
    if (!storeId) {
        // 如果沒有 storeId，清空列表並返回
        categories.splice(0);
        items.splice(0);
        return;
    }

    try {
        isLoading.value = true;
        error.value = null;

        console.log(`🚀 正在為店家 ID: ${storeId} 獲取菜單資料...`);
        // 使用 Promise.all 來並行發送所有請求，效率更高
        const [categoriesResponse, itemsResponse] = await Promise.all([
            apiClient.get(`/api/food-classes/store/${storeId}`),
            apiClient.get(`/api/foods/store/${storeId}`),
            // apiClient.get(`/specs/store/${storeId}`), // 未來可以加上規格的 API
        ]);

        // 【修改】使用 .splice(0) 和解構賦值來安全地更新 reactive 陣列
        categories.splice(0, categories.length, ...categoriesResponse.data);

        // 🔥 NEW: 轉換 items 資料格式，確保與前端元件相容
        const formattedItems = itemsResponse.data.map(item => {
            console.log('🔍 處理 item 資料:', item); // 除錯：查看原始資料

            // 🔥 關鍵修正：通過 categoryName 找到對應的 categoryId
            let categoryId = null;
            if (item.categoryName) {
                const matchedCategory = categoriesResponse.data.find(cat => 
                    cat.name === item.categoryName
                );
                if (matchedCategory) {
                    categoryId = matchedCategory.id;
                    console.log(`   📋 找到分類匹配: "${item.categoryName}" → ID: ${categoryId}`);
                } else {
                    console.warn(`   ⚠️ 找不到分類: "${item.categoryName}"`);
                }
            } else if (item.foodClassIds && item.foodClassIds.length > 0) {
                // 備用：如果有 foodClassIds，使用第一個
                categoryId = item.foodClassIds[0];
                console.log(`   📋 使用 foodClassIds: ${categoryId}`);
            }

            return {
                ...item,
                status: item.isActive ? '供應中' : '停售',  // 轉換後端的 isActive 為前端的 status
                categoryId: categoryId, // 🔥 使用找到的 categoryId
                imgResource: item.imgResource || item.imageUrl || item.image || '', // 確保圖片欄位存在
                imageUrl: item.imgResource || item.imageUrl || item.image || '' // 備用圖片欄位
            };
        });

        console.log('✅ 格式化後的 items:', formattedItems);
        items.splice(0, items.length, ...formattedItems);
        // specs.splice(0, specs.length, ...specsResponse.data);

        console.log('✅ 成功獲取分類:', categories);
        console.log('✅ 成功獲取品項:', items);

    } catch (e) {
        console.error(`❌ 獲取店家 ID:${storeId} 的資料失敗:`, e);
        error.value = '無法載入菜單資料，請稍後再試。';
        // 發生錯誤時清空資料
        categories.splice(0);
        items.splice(0);
    } finally {
        isLoading.value = false;
    }
};


// =================================================================
// 4. 生命週期鉤子和監聽器
// =================================================================
// onMounted(() => {
//     fetchMenuData(selectedStore.value);
// });

// 監聽 selectedStore 的變化，當使用者切換店家時，重新獲取資料
// watch(selectedStore, (newStoreId) => {
//     fetchMenuData(newStoreId);
// });

onMounted(async () => {
    // 🔥 NEW: 先載入資料和店家列表，再載入菜單資料
    await loadUserData()
    
    // 如果有選中的店家，就載入菜單資料
    if (selectedStore.value) {
        await fetchMenuData(selectedStore.value)
    }
});

// 【修改】監聽 selectedStore 的變化，當使用者切換店家時，重新獲取資料
watch(selectedStore, async (newStoreId, oldStoreId) => {
    console.log('👀 監聽到 selectedStore 變化:', { 
        from: oldStoreId, 
        to: newStoreId 
    });
    
    if (newStoreId && newStoreId !== oldStoreId) {
        console.log(`🔄 切換店家：從 ${oldStoreId} 到 ${newStoreId}，重新載入資料`);
        await fetchMenuData(newStoreId);
        
        // 🔥 NEW: 更新 localStorage 中的 storeId
        localStorage.setItem('storeId', String(newStoreId));
        console.log('💾 已更新 localStorage 中的 storeId:', newStoreId);
    }
});

// 🔥 NEW: 監聽 localStorage 變化 (當用戶重新登入時)
const handleStorageChange = async () => {
    await loadUserData()
    if (selectedStore.value) {
        await fetchMenuData(selectedStore.value)
    }
}

// 監聽 storage 事件 (跨分頁同步)
window.addEventListener('storage', handleStorageChange)

// 清理事件監聽器
onBeforeUnmount(() => {
    window.removeEventListener('storage', handleStorageChange)
})


// =================================================================
// 5. 品項管理相關 (Item Management)
// =================================================================

const isItemPanelOpen = ref(false); // 控制編輯品項 Modal 的開關

const currentEditingItem = ref(null); // 正在編輯的品項，null 代表是新增

const openItemPanel = (item) => {
    currentEditingItem.value = item ? { ...item } : null;
    isItemPanelOpen.value = true;
};

const closeItemPanel = () => {
    isItemPanelOpen.value = false;
};

const handleSaveItem = async (itemData) => {
    isLoading.value = true;
    try {
        let payload; // 先宣告一個 payload 變數

        // 🔥 新增：處理圖片上傳
        if (itemData.imageFile) {
            console.log('開始上傳圖片...');
            try {
                const imageUrl = await uploadImage(itemData.imageFile);
                console.log('圖片上傳成功，URL:', imageUrl);
                itemData.imgResource = imageUrl; // 將圖片 URL 加入到 itemData
            } catch (uploadError) {
                console.error('圖片上傳失敗:', uploadError);
                alert('圖片上傳失敗，請重試');
                return; // 如果圖片上傳失敗，就不繼續執行
            }
        }

        // 🔥 新增：處理圖片刪除
        if (itemData.deleteExistingImage) {
            console.log('使用者刪除了既有圖片');
            itemData.imgResource = ''; // 清空圖片 URL
            // 注意：這裡可以選擇是否要從 Firebase 刪除舊圖片
            // 目前先不刪除，避免複雜化
        }

        // 判斷是新增還是編輯
        if (itemData.id) {
            // 【編輯模式】
            // 直接使用 itemData 作為 payload 的基礎
            payload = { ...itemData };
            
            // 🔥 DEBUG: 加在這裡 - 編輯模式的狀態檢查
            console.log('=== 編輯模式 Debug ===');
            console.log('原始 itemData.status:', itemData.status);
            console.log('payload.status:', payload.status);

            // 將 categoryId 轉換為後端需要的 foodClassIds 陣列
            if (payload.categoryId) {
                payload.foodClassIds = [payload.categoryId];
            } else {
                payload.foodClassIds = []; // 如果沒有選擇分類，就送一個空陣列
            }
            delete payload.categoryId; // 移除掉後端不需要的 categoryId，保持 payload 乾淨

            // 🔥 新增：處理狀態轉換
            console.log('轉換前 payload.status:', payload.status);
            payload.isActive = payload.status === '供應中';
            console.log('轉換後 payload.isActive:', payload.isActive);

            // 🔥 新增：處理圖片欄位轉換
            if (payload.imageUrl && !payload.imgResource) {
                payload.imgResource = payload.imageUrl;
            }

            // 🔥 新增：清理不需要的欄位
            delete payload.categoryId;
            delete payload.imageFile;
            delete payload.deleteExistingImage;
            delete payload.imageUrl;  // ← 編輯模式也要清除
            delete payload.status;    // ← 新增：清除前端用的 status

            console.log("準備發送 PUT 請求的 payload:", payload);
            const response = await apiClient.put(`/api/foods/${itemData.id}`, payload);

            // 🔥 新增：檢查 API 回應
            console.log("API 回應狀態:", response.status);
            console.log("API 回應資料:", response.data);

            alert('品項更新成功！');

        } else {
            // 【新增模式】
            // 為 payload 加上 storeId
            payload = { ...itemData, storeId: selectedStore.value };

            // 🔥 DEBUG: 加在這裡 - 新增模式的狀態檢查
            console.log('=== 新增模式 Debug ===');
            console.log('原始 itemData.status:', itemData.status);
            console.log('payload.status:', payload.status);

            // 同樣，將 categoryId 轉換為 foodClassIds 陣列
            if (payload.categoryId) {
                payload.foodClassIds = [payload.categoryId];
            } else {
                payload.foodClassIds = [];
            }
            delete payload.categoryId; // 移除掉後端不需要的 categoryId，保持 payload 乾淨

            // 🔥 新增：處理狀態轉換
            console.log('轉換前 payload.status:', payload.status);
            payload.isActive = payload.status === '供應中';
            console.log('轉換後 payload.isActive:', payload.isActive);
            
            // 🔥 新增：處理圖片欄位轉換
            if (payload.imageUrl && !payload.imgResource) {
                payload.imgResource = payload.imageUrl;
            }

            // 🔥 新增：清理不需要的欄位
            delete payload.categoryId;
            delete payload.imageFile; // 移除 imageFile，只保留 imageUrl
            delete payload.deleteExistingImage; // 移除刪除標記
            delete payload.imageUrl; //  清除前端用的 imageUrl
            delete payload.status;    // ← 新增：清除前端用的 status

            console.log("準備發送 POST 請求的 payload:", payload);
            await apiClient.post('/api/foods', payload);
            alert('品項新增成功！');
        }
        
        // 操作成功後，重新獲取列表
        await fetchMenuData(selectedStore.value);

    } catch (e) {
        console.error('儲存品項失敗:', e);
        alert(`儲存失敗：${e.response?.data?.message || e.message}`);
    } finally {
        isLoading.value = false;
        closeItemPanel();
    }
};

const handleDeleteItem = async (itemId) => {
    if (confirm('確定要刪除此品項嗎？')) {
        isLoading.value = true;
        try {
            // 【修改】刪除：呼叫 DELETE API
            await apiClient.delete(`/api/foods/${itemId}`);
            alert('刪除成功！');
            await fetchMenuData(selectedStore.value); // 重新獲取列表
        } catch (e) {
            console.error('刪除品項失敗:', e);
            alert(`刪除失敗：${e.response?.data?.message || e.message}`);
        } finally {
            isLoading.value = false;
            closeItemPanel();
        }
    }
};

// =================================================================
// 6. 品項類別管理相關 (Category Management)
// =================================================================

// 控制編輯品項類別 Modal 的開關
const isCategoryPanelOpen = ref(false);

// 正在編輯的品項類別，null 代表是新增
const currentEditingCategory = ref(null);

const openCategoryPanel = (category) => {
    console.log('打開品項類別 Modal，編輯的資料是:', category);
    if (category) {
        // 編輯模式：直接複製資料
        currentEditingCategory.value = { ...category };
    } else {
        // 新增模式：計算新的 sort 值
        // 1. 找出當前最大的 sort 值
        const maxSort = categories.length > 0 
            ? Math.max(...categories.map(c => c.sort || 0)) 
            : 0;
        //categories.map(c => c.sort || 0): 遍歷所有類別，取出它們的 sort 值，如果某個類別的 sort 是 null 或 undefined，就當作 0。
        //這會得到一個像 [1, 3, 2] 這樣的數字陣列。
        //Math.max(...): ... 是展開運算符，它會把 [1, 3, 2] 展開成 Math.max(1, 3, 2)，這個函式會返回陣列中的最大值（3）。
        
        // 2. 建立一個包含預設 sort 值的新物件
        currentEditingCategory.value = {
            name: '',
            description: '',
            sort: maxSort + 1, // 新的 sort 值
        };
    }
    isCategoryPanelOpen.value = true;
};

const maxCategorySort = computed(() => {
    if (currentEditingCategory.value?.id) { // 編輯模式
        return categories.length;
    } else { // 新增模式
        return categories.length + 1;
    }

});

const closeCategoryPanel = () => {
    isCategoryPanelOpen.value = false;
}

const handleSaveCategory = async (categoryData) => {
    isLoading.value = true;
    try {
        if (categoryData.id) {
            // 編輯模式
            await apiClient.put(`/api/food-classes/${categoryData.id}`, categoryData);
            alert('類別更新成功！');
        } else {
            // 新增模式，記得加上 storeId
            const payload = { ...categoryData, storeId: selectedStore.value };
            await apiClient.post('/api/food-classes', payload);
            alert('類別新增成功！');
        }
        // 成功後，只重新獲取 categories 列表即可
        const response = await apiClient.get(`/api/food-classes/store/${selectedStore.value}`);
        categories.splice(0, categories.length, ...response.data);
    } catch (e) {
        console.error('儲存品項類別失敗:', e);
        alert(`儲存失敗：${e.response?.data?.message || e.message}`);
    } finally {
        isLoading.value = false;
        closeCategoryPanel(); // 使用您已有的關閉函式
    }
};

const handleDeleteCategory = async (categoryId) => {
    if (confirm('確定要刪除此品項類別嗎？\n刪除後，屬於此類別的品項將變為「未分類」。')) {
        isLoading.value = true;
        try {
            await apiClient.delete(`/api/food-classes/${categoryId}`);
            alert('刪除成功！');
            const response = await apiClient.get(`/api/food-classes/store/${selectedStore.value}`);
            categories.splice(0, categories.length, ...response.data);
        } catch (e) {
            console.error('刪除品項類別失敗:', e);
            alert(`刪除失敗：${e.response?.data?.message || e.message}`);
        } finally {
            isLoading.value = false;
            closeCategoryPanel();
        }
    }
};
const handleUpdateCategoryOrder = (updatedCategories) => {
    // 更新本地資料
    categories.splice(0, categories.length, ...updatedCategories);
    
    // 如果需要，可以在這裡呼叫 API 儲存新的排序
    // saveCategoryOrder(updatedCategories);
};

// =================================================================
// 6. 規格管理相關 (Specification Management) (可能來不及做)
// =================================================================

// 控制編輯規格 Modal 的開關
// const isSpecModalOpen = ref(false);

// 正在編輯的規格，null 代表是新增
// const currentEditingSpec = ref(null);

// const openSpecModal = (spec) => {
//     console.log('打開規格 Modal，編輯的資料是:', spec);
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
// 7. 通用方法 (General Methods)
// =================================================================

// 切換 Tab
const selectTab = (tab) => {
    activeTab.value = tab;
};

</script>

<template>
    <div>
        <!-- 使用新的公版 PageHeader 組件 -->
        <PageHeader title="菜單管理">
            <!-- 這是要 "塞" 進插槽的內容 -->
            <template #actions>
                <!-- 切換店鋪下拉選單 -->
                <!-- <select class="form-select" v-model="selectedStore" style="width: auto; min-width: 180px;">  
                    <option v-for="store in stores" :key="store.id" :value="store.id">
                        {{ store.name }}
                    </option>
                </select> -->

                <!-- 🔥 NEW: 顯示當前登入的用戶資訊 -->
                <div v-if="currentUser" class="d-flex align-items-center gap-3">
                    <span class="text-muted small">
                        管理者：{{ currentUser.ownerFullName || currentUser.ownerEmail }}
                    </span>
                    
                    <!-- 🔥 NEW: 支援多店家選擇 -->
                    <select v-if="stores.length > 1" 
                            class="form-select" 
                            v-model="selectedStore" 
                            style="width: auto; min-width: 200px;">
                        <option v-for="store in stores" :key="store.id" :value="store.id">
                            {{ store.name }}
                        </option>
                    </select>
                    
                    <!-- 單一店家時顯示店家名稱 -->
                    <div v-else-if="stores.length === 1" class="badge bg-primary fs-6">
                        {{ stores[0].name }}
                    </div>

                    <!-- 🔥 NEW: 顯示載入中狀態 -->
                    <div v-else-if="currentUser && stores.length === 0" class="badge bg-secondary">
                        <div class="spinner-border spinner-border-sm me-2" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        載入店家中...
                    </div>
                </div>
                
                <!-- 🔥 NEW: 如果沒有登入資料，顯示提示 -->
                <div v-else class="alert alert-warning mb-0" role="alert">
                    <small>⚠️ 未找到登入資料，請重新登入</small>
                </div>
            </template>
        </PageHeader>

        <!-- 使用完整的條件判斷鏈，確保邏輯正確 -->
        <div v-if="!selectedStore && !isLoading" class="alert alert-info">
            <h5>📋 沒有店家資料</h5>
            <p class="mb-0">
                請確認您已正確登入，並且帳號關聯了店家資料。<br>
                <small class="text-muted">
                    測試用戶可以在 Console 中執行 <code>setTempLogin(1)</code> 來設定測試資料。
                </small>
            </p>
        </div>
        
        <div v-else-if="isLoading" class="text-center p-5">
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
                    <a class="nav-link" :class="{ active: activeTab === 'categories' }" href="#"
                        @click.prevent="selectTab('categories')">品項類別管理</a>
                </li>
                <!-- <li class="nav-item">
                    <a class="nav-link" :class="{ active: activeTab === 'specs' }" href="#"
                        @click.prevent="selectTab('specs')">客製化規格</a>
                </li> -->
            </ul>

            <div class="mt-4">
                <MenuOverview v-if="activeTab === 'overview'" 
                    :items="items" 
                    :categories="categories"
                    @add-new-item="openItemPanel(null)" 
                    @edit-item="openItemPanel" />

                <CategoryManagement v-if="activeTab === 'categories'"
                    :categories="categories"
                    @addNewCategory="openCategoryPanel(null)"
                    @editCategory="openCategoryPanel"
                    @updateCategoryOrder="handleUpdateCategoryOrder"
                />

                <!-- <CustomizationSpecs v-if="activeTab === 'specs'" 
                    :specs="specs" 
                    @add-new-spec="openSpecModal(null)"
                    @edit-spec="openSpecModal" 
                /> --><!-- 客製化規格暫停開發 -->
            </div>
        </div>


        <!-- Modals (不受佈局影響，已套用共用面板SlideOutPanel) -->
        <SlideOutPanel v-model:isOpen="isItemPanelOpen"
            :title="currentEditingItem ? '編輯品項' : '新增品項'">
        
        <!-- 
        只有在 isItemPanelOpen 為 true (面板打開) 時，才渲染 EditItemPanel。
        這樣可以確保每次打開面板時，EditItemPanel 都會重新掛載，
        其內部的 watchEffect 會重新執行，正確地初始化表單資料。
        -->
            <EditItemPanel v-if="isItemPanelOpen" 
                :item="currentEditingItem" 
                :categories="categories"
                @close="isItemPanelOpen = false" 
                @save="handleSaveItem"
                @delete="handleDeleteItem" 
            />
        </SlideOutPanel>


        <SlideOutPanel v-model:isOpen="isCategoryPanelOpen"
            :title="currentEditingCategory ? '編輯品項類別' : '新增品項類別'"
        >
        
            <EditCategoryPanel v-if="isCategoryPanelOpen" 
                :category="currentEditingCategory" 
                :max-sort="maxCategorySort" 
                @close="closeCategoryPanel" 
                @save="handleSaveCategory"
                @delete="handleDeleteCategory" 
            />
        </SlideOutPanel>

        
        <!-- <SlideOutPanel v-model:isOpen="isSpecModalOpen"
            :title="currentEditingSpec ? '編輯客製化規格' : '新增客製化規格'"> -->
        
        <!-- 同樣只在 isSpecModalOpen 為 true 時，才渲染 EditSpecModal。-->

            <!-- <EditSpecModal v-if="isSpecModalOpen" 
                :spec="currentEditingSpec" 
                @close="isSpecModalOpen = false" 
                @save="handleSaveSpec"
                @delete="handleDeleteSpec" 
            />
        </SlideOutPanel> -->
    </div>
</template>

<style scoped>
/* 已搬到共用樣式 SellerLayout.vue */
</style>