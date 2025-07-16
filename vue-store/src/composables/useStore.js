// composables/useStore.js
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import apiClient from '@/plungins/axios.js'

// 🔥 全域狀態：所有使用 useStore 的組件都會共享這些狀態
const currentUser = ref(null)
const stores = ref([])
const selectedStore = ref(null)
const isLoading = ref(false)
const error = ref(null)

// 載入用戶資料和店家列表
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

        console.log('✅ [useStore] 載入用戶資料:', currentUser.value)

        try {
            console.log(`🚀 [useStore] 正在為 owner ID: ${ownerId} 獲取店家列表...`)

            const storesResponse = await apiClient.get('/api/stores/profile/all', {
                params: { ownerId: ownerId }
            })
            console.log('✅ [useStore] 成功獲取店家列表:', storesResponse.data)

            stores.value = storesResponse.data.map(store => ({
                id: store.id,
                name: store.name || store.storeName || `店家${store.id}`
            }))

            // 智慧選擇預設店家
            if (stores.value.length > 0) {
                const savedStoreId = localStorage.getItem('storeId')
                const savedStore = stores.value.find(store => String(store.id) === String(savedStoreId))

                if (savedStore) {
                    selectedStore.value = savedStore.id
                    console.log('📌 [useStore] 使用 localStorage 中的店家:', savedStore)
                } else {
                    selectedStore.value = stores.value[0].id
                    console.log('📌 [useStore] 選擇第一個店家:', stores.value[0])
                    // 更新 localStorage
                    localStorage.setItem('storeId', String(stores.value[0].id))
                }
            }

            console.log('🏪 [useStore] 最終店家狀態:', {
                stores: stores.value,
                selectedStore: selectedStore.value
            })

        } catch (err) {
            console.error('❌ [useStore] 獲取店家列表失敗:', err)
            error.value = `無法載入店家資料：${err.response?.data?.message || err.message}`
            stores.value = []
            selectedStore.value = null
        }
    } else {
        console.warn('⚠️ [useStore] 找不到 ownerId')
        currentUser.value = null
        stores.value = []
        selectedStore.value = null
    }
}

// 切換店家
const switchStore = async (newStoreId) => {
    const oldStoreId = selectedStore.value

    if (newStoreId && newStoreId !== oldStoreId) {
        console.log(`🔄 [useStore] 切換店家：從 ${oldStoreId} 到 ${newStoreId}`)
        selectedStore.value = newStoreId

        // 更新 localStorage
        localStorage.setItem('storeId', String(newStoreId))
        console.log('💾 [useStore] 已更新 localStorage 中的 storeId:', newStoreId)

        // 觸發重新載入事件（讓各頁面監聽）
        window.dispatchEvent(new CustomEvent('storeChanged', {
            detail: { newStoreId, oldStoreId }
        }))
    }
}

// 監聽 localStorage 變化（跨分頁同步）
const handleStorageChange = async () => {
    await loadUserData()
}

// 🔥 主要的 composable 函數
export function useStore() {
    // 計算屬性
    const isLoggedIn = computed(() => !!currentUser.value?.ownerId)
    const currentStoreName = computed(() => {
        if (!selectedStore.value) return null
        const store = stores.value.find(s => s.id === selectedStore.value)
        return store?.name || null
    })

    // 生命週期處理
    onMounted(async () => {
        console.log('🎬 [useStore] 組件掛載')

        // 如果還沒有載入過，就載入
        if (!currentUser.value && !isLoading.value) {
            isLoading.value = true
            try {
                await loadUserData()
            } finally {
                isLoading.value = false
            }
        }

        // 監聽 storage 事件
        window.addEventListener('storage', handleStorageChange)
    })

    onBeforeUnmount(() => {
        window.removeEventListener('storage', handleStorageChange)
    })

    // 重新載入資料
    const refreshData = async () => {
        isLoading.value = true
        try {
            await loadUserData()
        } finally {
            isLoading.value = false
        }
    }

    return {
        // 狀態
        currentUser: computed(() => currentUser.value),
        stores: computed(() => stores.value),
        selectedStore: computed(() => selectedStore.value),
        isLoading: computed(() => isLoading.value),
        error: computed(() => error.value),

        // 計算屬性
        isLoggedIn,
        currentStoreName,

        // 方法
        switchStore,
        refreshData,
        loadUserData: () => loadUserData()
    }
}