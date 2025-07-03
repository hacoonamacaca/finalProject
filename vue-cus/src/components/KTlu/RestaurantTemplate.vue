<template>
    <div class="restaurant-container restaurant-theme">
        <!-- 餐廳橫幅 -->
        <RestaurantBanner :restaurant="restaurant" />

        <!-- 餐廳名稱區域 -->
        <div class="restaurant-header">
            <h1 class="restaurant-main-title">{{ restaurant.name }}</h1>
        </div>

        <!-- TabMenu 區域 -->
        <div class="tab-menu-section restaurant-light-bg">
            <div class="tab-menu-container">
                <div class="tab-menu-tabs">
                    <button class="tab-menu-tab" :class="{ active: activeTab === 'reservation' }"
                        @click="activeTab = 'reservation'">
                        <i class="pi pi-calendar"></i>
                        <span>預約訂位</span>
                    </button>
                    <button class="tab-menu-tab" :class="{ active: activeTab === 'order' }"
                        @click="activeTab = 'order'">
                        <i class="pi pi-shopping-cart"></i>
                        <span>線上訂餐</span>
                    </button>
                </div>
            </div>
        </div>

        <!-- 餐廳資訊 -->
        <RestaurantInfo :restaurant="restaurant" />

        <!-- Tab 內容區域 -->
        <div class="tab-content-section">
            <!-- 預約訂位內容 -->
            <div v-if="activeTab === 'reservation'" class="tab-content" id="reservation-content">
                <ReservationForm :restaurant-id="restaurant.id.toString()" />
            </div>

            <!-- 線上訂餐內容 -->
            <div v-if="activeTab === 'order'" class="tab-content" id="order-content">
                <RestaurantMenu :restaurant="restaurant" @checkout="handleCheckout" />
            </div>
        </div>

        <!-- 地圖 -->
        <RestaurantMap :restaurant="restaurant" />

        <!-- 頁腳 -->
        <RestaurantFooter :restaurant="restaurant" />
    </div>
</template>

<script setup>
import { ref } from 'vue'
import RestaurantBanner from './RestaurantBanner.vue'
import RestaurantInfo from './RestaurantInfo.vue'
import RestaurantMenu from './RestaurantMenu.vue'
import RestaurantMap from './RestaurantMap.vue'
import ReservationForm from './ReservationForm.vue'
import RestaurantFooter from './RestaurantFooter.vue'
import '@/assets/css/restaurant-theme.css'

// 接收餐廳資料
defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

// Tab狀態管理
const activeTab = ref('order') // 預設顯示線上訂餐以展示Hero風格菜單導航

// 處理結帳
const handleCheckout = (orderData) => {
    console.log('處理結帳:', orderData)
    // 這裡可以添加結帳處理邏輯
    // 例如：跳轉到支付頁面、顯示結帳表單等
    alert(`訂單總計：NT$${orderData.totalAmount}\n正在處理訂單...`)
}
</script>

<style scoped>
.restaurant-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0;
    background: var(--restaurant-bg-primary);
    box-shadow: 0 0 20px var(--restaurant-shadow-light);
}

.restaurant-header {
    text-align: left;
    padding: 2rem 1rem;
    background: var(--restaurant-gradient-subtle);
    width: 100%;
    margin: 0 0;
    border-bottom: 2px solid var(--restaurant-border-light);
}

.restaurant-main-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: var(--restaurant-text-primary);
    margin: 0;
    letter-spacing: 1px;
    text-shadow: 0 1px 2px var(--restaurant-shadow-neutral);
}

/* TabMenu 樣式 - 優雅主題 */
.tab-menu-section {
    background: var(--restaurant-bg-secondary);
    padding: 1rem;
    border-bottom: 2px solid var(--restaurant-border-light);
}

.tab-menu-container {
    max-width: 600px;
    margin: 0 auto;
    text-align: center;
}

.tab-menu-tabs {
    display: inline-flex;
    background: var(--restaurant-bg-primary);
    border: 1px solid var(--restaurant-border-medium);
    border-radius: 8px;
    padding: 0.25rem;
    gap: 0.25rem;
    box-shadow: 0 2px 8px var(--restaurant-shadow-neutral);
}

.tab-menu-tab {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    background: transparent;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 0.9rem;
    color: var(--restaurant-text-secondary);
    font-weight: 500;
}

.tab-menu-tab:hover {
    background: var(--restaurant-bg-accent);
    color: var(--restaurant-text-primary);
    transform: translateY(-1px);
}

.tab-menu-tab.active {
    background: var(--restaurant-text-secondary);
    color: var(--restaurant-bg-primary);
    box-shadow: 0 3px 12px var(--restaurant-shadow-medium);
    transform: translateY(-1px);
}

.tab-menu-tab i {
    font-size: 1.1rem;
}

.tab-menu-tab span {
    font-weight: 500;
}

/* Tab 內容區域 - 黃白主題 */
.tab-content-section {
    background: var(--restaurant-bg-primary);
    width: 100%;
    margin: 0;
    padding: 0;
    border-top: 1px solid var(--restaurant-border-light);
}

.tab-content {
    padding: 1.5rem 1rem;
    scroll-margin-top: 80px;
    background: var(--restaurant-bg-primary);
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-main-title {
        font-size: 2rem;
    }

    .restaurant-header {
        padding: 1.5rem 1rem;
        width: 100%;
    }

    .tab-menu-section {
        padding: 0.75rem;
    }

    .tab-menu-title {
        font-size: 1.25rem;
    }

    .tab-menu-subtitle {
        font-size: 0.8rem;
    }

    .tab-menu-tabs {
        gap: 0.2rem;
        margin-top: 0.75rem;
    }

    .tab-menu-tab {
        padding: 0.65rem 1.25rem;
        font-size: 0.85rem;
    }

    .tab-menu-tab i {
        font-size: 1rem;
    }

    .tab-content {
        padding: 1.25rem 0.75rem;
    }

    .content-header h3 {
        font-size: 1.1rem;
    }

    .content-header p {
        font-size: 0.8rem;
    }
}

@media (max-width: 480px) {
    .restaurant-main-title {
        font-size: 1.75rem;
    }

    .restaurant-header {
        width: 100%;
    }

    .tab-menu-section {
        padding: 0.5rem;
    }

    .tab-menu-container {
        padding: 0;
    }

    .tab-menu-title {
        font-size: 1.15rem;
    }

    .tab-menu-subtitle {
        font-size: 0.75rem;
    }

    .tab-menu-tabs {
        margin-top: 0.75rem;
        padding: 0.2rem;
    }

    .tab-menu-tab {
        padding: 0.6rem 1rem;
        font-size: 0.8rem;
    }

    .tab-menu-tab i {
        font-size: 0.95rem;
    }

    .tab-content {
        padding: 1rem 0.5rem;
    }

    .content-header {
        padding: 0;
        margin-bottom: 0.75rem;
    }

    .content-header h3 {
        font-size: 1rem;
    }

    .content-header p {
        font-size: 0.75rem;
    }
}
</style>